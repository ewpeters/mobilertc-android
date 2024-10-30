package us.zoom.sdksample.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdksample.R;

public class ShareListDialog extends BottomSheetDialogFragment {

    public interface ClickListener {
        void onUserClicked(ZoomVideoSDKUser user);
    }

    private List<ZoomVideoSDKUser> shareUserList;
    private ClickListener clickListener;

    public static void show(FragmentActivity activity, List<ZoomVideoSDKUser> shareUser, ClickListener listener) {
        ShareListDialog dialog = new ShareListDialog();
        dialog.shareUserList = shareUser;
        dialog.clickListener = listener;
        dialog.show(activity.getSupportFragmentManager(), "ShareListDialog");
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bottom_share_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get a reference to the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // Create and set up your RecyclerView adapter and layout manager
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(); // Create your adapter
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Bind your data to the ViewHolder's views
            holder.textView.setText(shareUserList.get(position).getUserName());
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareListDialog.this.dismiss();
                    if (clickListener != null) {
                        clickListener.onUserClicked(shareUserList.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return shareUserList == null ? 0 : shareUserList.size(); // Number of items in the RecyclerView
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.itemText);
            }
        }
    }

}
