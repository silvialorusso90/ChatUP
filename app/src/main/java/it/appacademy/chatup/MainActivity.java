package it.appacademy.chatup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.appacademy.chatup.model.Messaggio;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    //UI
    private EditText mInputText;
    private Button mButtonInvia;


    @Override
    protected void onStart() {
        super.onStart();

        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO 2: Ricevere i dati dell'intent ed estrarli con il metodo getExtras()
        Bundle b = getIntent().getExtras();
        String extra = b.getString("msg");
        mAuth = FirebaseAuth.getInstance();
        setTitle(mAuth.getCurrentUser().getDisplayName());
        
        initUI();

        //Riferimento alla locazione del database generale
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // TODO 3: Presentare dati all'utente attraverso un Toast
        Toast.makeText(this,"Utente : "+ extra, Toast.LENGTH_SHORT).show();


        // TODO: Recuperare i dati salvati locamente nelle sharedpreferences e presentarli nella finestra di log
        //GetNomeOnPref();


    }

    private void initUI() {
        mInputText = (EditText) findViewById(R.id.et_messaggio);
        mButtonInvia = (Button) findViewById(R.id.btn_invia);

        //Tasto enter
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                inviaMessaggio();
                return true;
            }
        });

        mButtonInvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviaMessaggio();

            }
        });
    }

    private void inviaMessaggio() {
        Log.i("ChatUp", "inviaMessaggio");

        String inputMsg = mInputText.getText().toString();
        if(!inputMsg.equals("")){

            //Nuovo oggetto di tipo messaggio
            Messaggio chat = new Messaggio(inputMsg, mAuth.getCurrentUser().getDisplayName());

            //Salvare il messaggio sul Database cloud
            mDatabaseReference.child("messaggi").push().setValue(chat);

            mInputText.setText("");
        }
    }

    private void GetNomeOnPref() {

        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);

        String nomeOnPrefs = prefs.getString(RegisterActivity.NOME_KEY, null);

        Log.i("GetNomeOnPref", nomeOnPrefs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.layout_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logoutItem){

            Log.i(TAG, "Logout selezionato");
            // TODO: Logout
            mAuth.signOut();
            updateUI();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent intToLogin = new Intent(this, LoginActivity.class);
            finish();

            startActivity(intToLogin);
        }

    }
}
