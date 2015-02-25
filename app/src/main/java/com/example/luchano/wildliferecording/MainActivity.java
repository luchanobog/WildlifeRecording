package com.example.luchano.wildliferecording;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.net.Uri;
import android.view.ContextMenu.ContextMenuInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final int DELETE = 1;
    private static final int SHARE = 2;
    private static final int EDIT = 3;
    private static final int MAP = 4;
    EditText nameTxt, numberTxt, locationTxt, commentsTxt;
    ImageView imgViewSpeciesImage;
    List<Log> Log = new ArrayList<Log>();
    ListView logListView;
    Uri imageURI = Uri.parse("android.resource://com.example.luchano.wildliferecording/Drawable/galleryicon.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Log> logAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        //Declare all the text fields and assign them to their ID's
        nameTxt = (EditText) findViewById(R.id.nameTxt);
        numberTxt = (EditText) findViewById(R.id.numberTxt);
        locationTxt = (EditText) findViewById(R.id.locationTxt);
        commentsTxt = (EditText) findViewById(R.id.commentsTxt);
        logListView = (ListView) findViewById(R.id.listView);
        imgViewSpeciesImage = (ImageView) findViewById(R.id.imgViewSpeciesImage);
        //Instantiate the dbHandler so methods from that class can be used
        dbHandler = new DatabaseHandler(getApplicationContext());


        registerForContextMenu(logListView);
        logListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) //Callback method to be invoked when an item in this AdapterView has been clicked.
            {
                longClickedItemIndex = position;
                return false;
            }
        });

        //Setting up the add button
        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log log = new Log
                        (
                                dbHandler.getLogCount(),
                                String.valueOf(nameTxt.getText()),//get the value of the "Name" textbox
                                String.valueOf(numberTxt.getText()),
                                String.valueOf(locationTxt.getText()),
                                String.valueOf(commentsTxt.getText()),
                                imageURI);
                if (!logExists(log)) {
                    dbHandler.createLog(log);
                    Log.add(log);
                    logAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + "has been added to your logs!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " already exists. Please use a different name.", Toast.LENGTH_SHORT);
            }


        });

        //Explicitly launch Google Map
        final Button getLocation = (Button) findViewById(R.id.getLocation);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent2);
            }
        });

        //Configure the tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        //First tab configurations
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Add Data");
        tabHost.addTab(tabSpec);
        //second tab configurations
        tabSpec = tabHost.newTabSpec("log");
        tabSpec.setContent(R.id.tabLogCreator);
        tabSpec.setIndicator("Log");
        tabHost.addTab(tabSpec);
        //third tab for the map
//        tabSpec = tabHost.newTabSpec("Map");
//        tabSpec.setContent(R.id.tabMap);
//        tabSpec.setIndicator("Map");
//        tabHost.addTab(tabSpec);


        //The following methods disable the ADD Button if the name field is empty, there has to be a value
        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addBtn.setEnabled(String.valueOf(nameTxt.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Allow the user to pick an image from the gallery
        //the type is set to *, which indicates that any image type can be selected
        //TODO allow the user to take a picture and store it as an image
        imgViewSpeciesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a switch statement
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose a picture"), 1);
//                startActivityForResult(intent,CAMERA_RESULT);
            }
        });
        if (dbHandler.getLogCount() != 0)
            Log.addAll(dbHandler.getAllLogs());
        populateLog();

    }

    //Gets the image that has been selected by the user and adds it to the database
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageURI = data.getData();
                imgViewSpeciesImage.setImageURI(data.getData());
            }
        }
    }


    //Delete menu appears when an items is longClicked
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("Options");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Entry");
        menu.add(Menu.NONE, SHARE, menu.NONE, "Share");
        menu.add(Menu.NONE, MAP, menu.NONE, "Display on map");
    }

    //This code allows the user to delete an item from the database
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE:
                dbHandler.deleteLog(Log.get(longClickedItemIndex));//know exactly which one is selected
                Log.remove(longClickedItemIndex);//delete the selected item
                logAdapter.notifyDataSetChanged();//notify the adapter
                break;
            case SHARE:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Species retrieved");
                startActivity(intent);
                break;
            case MAP:
                Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //Checks if user has stored this value before and does not let them store it again
    //in order to avoid duplication
    private boolean logExists(Log log) {
        String name = log.get_name();
        int logCount = Log.size();
        for (int i = 0; i < logCount; i++) {
            if (name.compareToIgnoreCase(Log.get(i).get_name()) == 0)
                return true;
        }
        return false;
    }

    //Assign an adapter for the list
    private void populateLog() {
        logAdapter = new LogListAdapter();
        logListView.setAdapter(logAdapter);
    }


    //Adapter that converts the Java objects from the Log class to a View
    private class LogListAdapter extends ArrayAdapter<Log> {
        //Constructor for this class
        public LogListAdapter() {
            super(MainActivity.this, R.layout.listview_item, Log);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {


            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
            Log currentLog = Log.get(position);
            //ViewHolder viewHolder;
            TextView name = (TextView) view.findViewById(R.id.speciesName);
            name.setText(currentLog.get_name());
            TextView number = (TextView) view.findViewById(R.id.speciesNumber);
            number.setText(currentLog.get_number());
            TextView location = (TextView) view.findViewById(R.id.speciesLocation);
            location.setText(currentLog.get_location());
            TextView comments = (TextView) view.findViewById(R.id.speciesComments);
            comments.setText(currentLog.get_comments());
            ImageView ivSpeciesImage = (ImageView) view.findViewById(R.id.ivSpeciesImage);
            ivSpeciesImage.setImageURI(currentLog.get_imageURI());

            return view;
        }
    }


}
