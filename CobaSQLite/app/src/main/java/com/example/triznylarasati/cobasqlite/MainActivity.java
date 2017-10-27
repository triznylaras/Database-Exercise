package com.example.triznylarasati.cobasqlite;

import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DatabaseHelper myDB;
    EditText name, surname, marks, id;
    Button save_btn, list_btn, update_btn, delete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inisiasi floating button dan fungsinya
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //inisiasi databasehelper class dengan nama myDB dan isinya
        myDB = new DatabaseHelper(this);
        name = (EditText)findViewById(R.id.name_txt);
        surname = (EditText)findViewById(R.id.surname_txt);
        marks = (EditText)findViewById(R.id.marks_txt);
        id = (EditText)findViewById(R.id.id_txt);

        save_btn = (Button)findViewById(R.id.send_btn);
        list_btn = (Button)findViewById(R.id.students_btn);
        update_btn = (Button)findViewById(R.id.update_btn);
        delete_btn = (Button)findViewById(R.id.delete_btn);

        save_btn.setOnClickListener(this);
        list_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //case utk save button
            case R.id.send_btn :
                boolean result = myDB.save_student(id.getText().toString(), name.getText().toString(),
                        surname.getText().toString(),
                        marks.getText().toString()
                );
                if (result)
                    Toast.makeText(MainActivity.this, "Success Add Student", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Fails Add Student", Toast.LENGTH_LONG).show();
                break;

            //case utk students button
            case R.id.students_btn :
                Cursor students = myDB.list_student();

                //jika belum ada data student yang dimasukkan
                if (students.getCount() == 0) {
                    alert_message("Message", "No data student found");
                    return;
                }

                //append data student to buffer
                StringBuilder buffer = new StringBuilder();
                while (students.moveToNext()) {
                    buffer.append("ID : ").append(students.getString(0)).append("\n");
                    buffer.append("Name : ").append(students.getString(1)).append("\n");
                    buffer.append("Surname : ").append(students.getString(2)).append("\n");
                    buffer.append("Marks : ").append(students.getString(3)).append("\n\n\n");
                }

                //show data student
                alert_message("List Students", buffer.toString());
                break;

            //case utk update button
            case R.id.update_btn :
                boolean result2 = myDB.update_student(name.getText().toString(),
                        surname.getText().toString(),
                        marks.getText().toString(), id.getText().toString());
                if (result2)
                    Toast.makeText(MainActivity.this, "Success update data Student", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Fails update data Student", Toast.LENGTH_LONG).show();
                break;

            //case utk delete button
            case R.id.delete_btn :
                Integer result3 = myDB.delete_student(id.getText().toString());
                //jika data student lebih dari 0 maka bisa didelete
                if (result3 > 0)
                    Toast.makeText(MainActivity.this, "Success delete a Student", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Fails delete a Student", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void alert_message(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
