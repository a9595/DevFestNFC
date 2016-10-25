package tieorange.com.devfestnfc;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class MainActivity extends SuperMainActivity {
  @BindView(R.id.recycler) RecyclerView mRecyclerView;
  private String TAG = MainActivity.class.getCanonicalName();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initRecycler();
  }

  private void initRecycler() {
    mRecyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
    layoutManager.setStackFromEnd(true);
    mRecyclerView.setLayoutManager(layoutManager);
    initAdapter();
  }

  private void initAdapter() {
    FirebaseRecyclerAdapter<Person, PersonViewHolder> adapter =
        new FirebaseRecyclerAdapter<Person, PersonViewHolder>(Person.class, R.layout.item_person, PersonViewHolder.class,
            mDatabaseReferencePeople.orderByChild("points")) {
          @Override protected void populateViewHolder(PersonViewHolder viewHolder, Person model, int position) {
            viewHolder.mPosition.setText(String.valueOf(model.points));
            viewHolder.mUserId.setText(String.valueOf(model.id));
          }
        };

    mRecyclerView.setAdapter(adapter);
  }

  @OnClick(R.id.fab) public void onClickFAB() {
    final String nfcId = "123";
    givePointsToPerson(nfcId);
  }
}
