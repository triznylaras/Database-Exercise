package com.example.triznylarasati.firebaserealtimedatabase;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String AUTHOR_NAME = "com.example.triznylarasati.firebaserealtimedatabase.authorname";
    public static final String AUTHOR_ID =  "com.example.triznylarasati.firebaserealtimedatabase.authorid";

    EditText editTextName;
    Spinner spinnerGenre;
    Button buttonAddAuthor;
    ListView listViewAuthors;

    //a list to store all the authors from firebase database
    List<Author> authors;

    //our database reference object
    DatabaseReference databaseAuthors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting the reference of authors node
        databaseAuthors = FirebaseDatabase.getInstance().getReference("authors");

        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        spinnerGenre = (Spinner) findViewById(R.id.spinnerGenres);
        listViewAuthors = (ListView) findViewById(R.id.listViewAuthor);

        buttonAddAuthor = (Button) findViewById(R.id.buttonAddAuthor);

        //list to store authors
        authors = new ArrayList<>();

        //adding an onclicklistener to button
        buttonAddAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling the method addAuthors()
                //the method is defined below
                //this method is actually performing the write operation
                addAuthor();
            }
        });

        //attaching listener to listview
        listViewAuthors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected author
                Author author = authors.get(i);

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), Author.class);

                //putting author name and id to intent
                intent.putExtra(AUTHOR_ID, author.getAuthorId());
                intent.putExtra(AUTHOR_NAME, author.getAuthorName());

                //starting the activity with intent
                startActivity(intent);
            }
        });

        listViewAuthors.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Author author = authors.get(i);
                showUpdateDeleteDialog(author.getAuthorId(), author.getAuthorName());
                return true;
            }
        });
    }

    private void showUpdateDeleteDialog(final String authorId, String authorName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Spinner spinnerGenre = (Spinner) dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateAuthor);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteAuthor);

        dialogBuilder.setTitle(authorName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getSelectedItem().toString();
                if (!TextUtils.isEmpty(name)) {
                    updateAuthor(authorId, name, genre);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAuthor(authorId);
                b.dismiss();
            }
        });
    }

    private boolean updateAuthor (String id, String name, String genre) {
        //getting the specified author reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("authors").child(id);

        //updating author
        Author author = new Author(id, name, genre);
        dR.setValue(author);
        Toast.makeText(getApplicationContext(), "Author Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteAuthor (String id) {
        //getting the specified author reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("authors").child(id);

        //removing author
        dR.removeValue();

        //getting the books reference for the specified author
        DatabaseReference drBooks = FirebaseDatabase.getInstance().getReference("books").child(id);

        //removing all books
        drBooks.removeValue();
        Toast.makeText(getApplicationContext(), "Author Deleted", Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //attaching value event listener
        databaseAuthors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous author list
                authors.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting author
                    Author author = postSnapshot.getValue(Author.class);
                    //adding author to the list
                    authors.add(author);
                }

                //creating adapter
                AuthorList authorAdapter = new AuthorList(MainActivity.this, authors);
                //attaching adapter to the listview
                listViewAuthors.setAdapter(authorAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    This method is saving a new author to the
    Firebase Realtime Database
     */
    private void addAuthor() {
        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        //checking if the value is provided
        if(!TextUtils.isEmpty(name)) {

            //getting an unique id using push().getKey() method
            //it will create an unique id and we will use it as the Primary Key for our author
            String id = databaseAuthors.push().getKey();

            //creating an Author Object
            Author author = new Author(id, name, genre);

            //saving the author
            databaseAuthors.child(id).setValue(author);

            //setting edittext to blank again
            editTextName.setText("");

            //displaying a success toast
            Toast.makeText(this, "Author added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
