package com.example.triznylarasati.firebaserealtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AuthorActivity extends AppCompatActivity{
    Button buttonAddBook;
    EditText editTextBookName;
    SeekBar seekBarRating;
    TextView textViewRating, textViewAuthor;
    ListView listViewBook;

    DatabaseReference databaseBooks;

    List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        Intent intent = getIntent();

        /*
        * this line is important
        * this time we are not getting the reference of a direct node
        * but inside the node book we are creating a new child with the author id
        * and inside that node we will store all the books with unique ids
        * */
        databaseBooks = FirebaseDatabase.getInstance().getReference("books").child(intent.getStringExtra(MainActivity.AUTHOR_ID));

        buttonAddBook = (Button) findViewById(R.id.buttonAddBooks);
        editTextBookName = (EditText) findViewById(R.id.editTextName);
        seekBarRating = (SeekBar) findViewById(R.id.seekBarRating);
        textViewRating = (TextView) findViewById(R.id.textViewRating);
        textViewAuthor = (TextView) findViewById(R.id.textViewAuthor);
        listViewBook = (ListView) findViewById(R.id.listViewBooks);

        books = new ArrayList<>();

        textViewAuthor.setText((intent.getStringExtra(MainActivity.AUTHOR_NAME)));

        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewRating.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                books.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Book book = postSnapshot.getValue(Book.class);
                    books.add(book);
                }
                BookList bookListAdapter = new BookList(AuthorActivity.this, books);
                listViewBook.setAdapter(bookListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void saveBook() {
        String bookName = editTextBookName.getText().toString().trim();
        int rating = seekBarRating.getProgress();
        if (!TextUtils.isEmpty(bookName)) {
            String id = databaseBooks.push().getKey();
            Book book = new Book(id, bookName, rating);
            databaseBooks.child(id).setValue(book);
            Toast.makeText(this, "Book saved", Toast.LENGTH_LONG).show();
            editTextBookName.setText("");
        } else {
            Toast.makeText(this, "Please enter book name", Toast.LENGTH_LONG).show();
        }
    }
}
