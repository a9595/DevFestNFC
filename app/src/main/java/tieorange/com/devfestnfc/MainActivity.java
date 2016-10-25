package tieorange.com.devfestnfc;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class MainActivity extends SuperMainActivity {
  @BindView(R.id.recycler) RecyclerView mRecyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initRecycler();
  }

  private void initRecycler() {
    mRecyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
    layoutManager.setStackFromEnd(true);
    mRecyclerView.setLayoutManager(layoutManager);

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
    mDatabaseReferencePeople.child("888").setValue(new Person(888, 3));
  }
}
