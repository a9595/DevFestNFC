package tieorange.com.devfestnfc;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tieorange on 25/10/2016.
 */
public class PersonViewHolder extends RecyclerView.ViewHolder {
  @BindView(R.id.position) TextView mPosition;
  @BindView(R.id.userId) TextView mUserId;

  public PersonViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }
}

