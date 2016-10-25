package tieorange.com.devfestnfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import be.appfoundry.nfclibrary.utilities.sync.NfcReadUtilityImpl;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by tieorange on 25/10/2016.
 */
public class SuperMainActivity extends AppCompatActivity {
  private static final String TAG = SuperMainActivity.class.getCanonicalName();
  private PendingIntent mPendingIntent;
  private IntentFilter[] mIntentFilters;
  private String[][] mTechLists;
  private NfcAdapter mNfcAdapter;
  public FirebaseDatabase mDatabase;
  private DatabaseReference mDatabaseReference;
  public DatabaseReference mDatabaseReferencePeople;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    initFirebase();

    initNFC();
  }

  public void onResume() {
    super.onResume();
    if (mNfcAdapter != null) {
      mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mTechLists);
    }
  }

  public void onPause() {
    super.onPause();
    if (mNfcAdapter != null) {
      mNfcAdapter.disableForegroundDispatch(this);
    }
  }

  private void initFirebase() {
    // Write a message to the database
    mDatabase = FirebaseDatabase.getInstance();
    mDatabaseReferencePeople = mDatabase.getReference("people");
  }

  protected void givePointsToExistingPerson(String personId) {
    mDatabaseReference = mDatabase.getReference("people/" + personId);

    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        Person person = dataSnapshot.getValue(Person.class);
        if (person == null) return;

        person.points++;
        mDatabaseReference.setValue(person, new DatabaseReference.CompletionListener() {
          @Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            Toast.makeText(SuperMainActivity.this, R.string.point_added, Toast.LENGTH_SHORT).show();
          }
        });
      }

      @Override public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  private void initNFC() {
    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    mIntentFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) };
    mTechLists = new String[][] {
        new String[] { Ndef.class.getName() }, new String[] { NdefFormatable.class.getName() }
    };
  }

  private void initFAB() {
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
      }
    });
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    for (final String message : new NfcReadUtilityImpl().readFromTagWithMap(intent).values()) {
      showDialog(message, new IPositiveDialog() {
        @Override public void positive() {
          givePointsToPerson(message);
        }
      });

      return;
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  protected void givePointsToNonexistentPerson(String nfcId) {
    mDatabaseReferencePeople.child(String.valueOf(nfcId)).setValue(new Person(nfcId, 1), new DatabaseReference.CompletionListener() {
      @Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        Toast.makeText(SuperMainActivity.this, R.string.point_added, Toast.LENGTH_SHORT).show();
      }
    });
  }

  protected void givePointsToPerson(final String nfcId) {
    // get value of current points:
    DatabaseReference reference = mDatabase.getReference("people/" + nfcId);
    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
        Person person = dataSnapshot.getValue(Person.class);
        if (person == null) {
          givePointsToNonexistentPerson(nfcId);
        } else {
          givePointsToExistingPerson(person.id);
        }
      }

      @Override public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
      }
    });
  }

  public void showDialog(String userId, final IPositiveDialog iPositiveDialog) {
    new MaterialDialog.Builder(this).title("Give points to id# " + userId)
        .positiveText("Oh YESS!!!")
        .negativeText("No")
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            iPositiveDialog.positive();
          }
        })
        .show();
  }
}
