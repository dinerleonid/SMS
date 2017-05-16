package com.leon.text.findandtext;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


public class ImportContacts extends AppCompatActivity {

    private  static final int PICK_CONTACT = 1;
    private String selectedName, selectedNumber, selectedEmail, id;
    private Cursor cursor, nameCursor, phoneCursor, emailCursor;
    private AlertDialog addAnotherContact;
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callContacts();
    }

    public void callContacts(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
       finish();

       // super.onBackPressed();

//
//        Intent in = new Intent(ImportContacts.this, LandingScreen.class);
//        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        startActivity(in);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0){
            onBackPressed();
        }
        sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
        edit = sp.edit();
        if(requestCode == PICK_CONTACT){
            if (resultCode == AppCompatActivity.RESULT_OK){
                Uri contactData = data.getData();
                cursor = getContentResolver().query(contactData, null, null, null, null);
                ContentResolver resolver = getContentResolver();
                while (cursor.moveToNext()){
                    id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    nameCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null,
                            null, null);
                    phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    selectedName = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    while (phoneCursor.moveToNext()) {


                        selectedNumber = phoneCursor.getString(phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                    }
                    while (emailCursor.moveToNext()) {
                        selectedEmail = emailCursor.getString((emailCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Email.DATA)));
                    }
                }
                cursor.close();
                nameCursor.close();
                phoneCursor.close();
                emailCursor.close();

                addAnotherContact = new AlertDialog.Builder(this).
                        setMessage(getString(R.string.you_have_selected_to_send) + "\n" + selectedName).
                        setTitle(R.string.contact_selection).
                        setPositiveButton(R.string.yes_in_contact_selection, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (selectedNumber == null) {
                                    Toast.makeText(ImportContacts.this, R.string.no_phone_number, Toast.LENGTH_SHORT).show();
                                    callContacts();

                                }else {
                                    edit.putString("name", selectedName);
                                    edit.putString("number", selectedNumber);
                                    edit.putString("email", selectedEmail);
                                    edit.commit();
                                    LandingScreen.selectedRecipient.setText(selectedName.toString() + "\n" + selectedNumber.toString());
                                    finish();
                                }

                            }
                        }).
                        setNegativeButton(R.string.no_in_contact_selection, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addAnotherContact.cancel();
                                callContacts();
                            }
                        }).
                        setCancelable(true).
                        create();
                addAnotherContact.show();


            }
//            cursor.close();
//            nameCursor.close();
//            phoneCursor.close();
//            emailCursor.close();
        }


        //onBackPressed();



    }
//     @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Log.d(this.getClass().getName(), "back button pressed");
//            onBackPressed();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
