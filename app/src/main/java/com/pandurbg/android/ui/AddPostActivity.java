package com.pandurbg.android.ui;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.pandurbg.android.R;
import com.pandurbg.android.db.PostRepository;
import com.pandurbg.android.model.Location;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.model.PostCategory;
import com.pandurbg.android.model.User;
import com.pandurbg.android.util.DummyData;

import java.util.ArrayList;

public class AddPostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    // must be the same as the categories in the string-array values resource
    public static final String CATEGORY_POLICE = "Police";
    public static final String CATEGORY_DANGER = "Road danger";
    public static final String CATEGORY_CAMERA = "Speed camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();

    }

    private void init(){


        Spinner spinner = findViewById(R.id.sCategory);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ImageButton ibAddPost = findViewById(R.id.ibAddPost);
        ibAddPost.setOnClickListener(this);
    }




    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.ibAddPost:
                addPostToBase();
                break;

        }

    }

    public void addPostToBase(){

        EditText etStreet = findViewById(R.id.etStreet);
        EditText etDescription = findViewById(R.id.etDescription);
        Spinner sCategories = findViewById(R.id.sCategory);

      //add post to database
        PostRepository.getInstance(AddPostActivity.this).
                addNewPost(getPostCategoryFromSpinner(sCategories.getSelectedItem().toString()),
                        etDescription.getText().toString(), etStreet.getText().toString(),
                        44, 21);

        Log.d("Post category", getPostCategoryFromSpinner(sCategories.getSelectedItem().toString()).getName());
        finish();
        }





    public PostCategory getPostCategoryFromSpinner(String currentSpinnerContent){

        PostCategory category= new PostCategory();

        switch(currentSpinnerContent){

            case CATEGORY_POLICE:
                    category.set_id(0);
                category.setName("Police Alert");
                category.setSlug("police");
                break;
            case CATEGORY_DANGER:
                category.set_id(1);
                category.setName("Danger alert");
                category.setSlug("danger");
                break;
            case CATEGORY_CAMERA:
                category.set_id(2);
                category.setName("camera Alert");
                category.setSlug("police");
                break;
                default:
                    Log.d("Invalid spinner entry", "Non existing category");
        }
        return category;
    }

}
