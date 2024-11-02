package us.zoom.sdksample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKErrors;
import us.zoom.sdk.ZoomVideoSDKVirtualBackgroundDataType;
import us.zoom.sdk.ZoomVideoSDKVirtualBackgroundHelper;
import us.zoom.sdk.ZoomVideoSDKVirtualBackgroundItem;

public class VBActivity extends AppCompatActivity {

    private static final int VB_REQUEST_SELECT_ORIGINAL_PIC = 2000;

    private final ZoomVideoSDKVirtualBackgroundHelper vbHelper = ZoomVideoSDK.getInstance().getVirtualBackgroundHelper();

    private RecyclerView vbRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vbactivity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (vbHelper == null) {
            finish();
            return;
        }

        vbRecycleView = findViewById(R.id.vbRecycleView);
        if (vbRecycleView == null) {
            finish();
            return;
        }


        initRecycleView();
        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFromGallery();
            }
        });
    }

    private static final int COLUMNS_NUM = 3;

    private void initRecycleView() {
        vbRecycleView.setLayoutManager(new GridLayoutManager(this, COLUMNS_NUM));
        int spacing = 20;
        vbRecycleView.addItemDecoration(new GridSpacingItemDecoration(COLUMNS_NUM, spacing, false));
        ImageAdapter imageAdapter = new ImageAdapter(this, vbHelper.getVirtualBackgroundItemList());
        vbRecycleView.setAdapter(imageAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VB_REQUEST_SELECT_ORIGINAL_PIC:
                if (resultCode== RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        if (null != selectedImage) {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImage);

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            ZoomVideoSDKVirtualBackgroundItem vbItem = vbHelper.addVirtualBackgroundItem(bitmap);
                            if (vbItem != null) {
                                finish();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void selectFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, VB_REQUEST_SELECT_ORIGINAL_PIC);
    }

    public static void startActivity(@NonNull Activity activity) {
        Intent intent = new Intent(activity, VBActivity.class);
        activity.startActivity(intent);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        private Context context;
        private List<ZoomVideoSDKVirtualBackgroundItem> imageItems;

        public ImageAdapter(Context context, List<ZoomVideoSDKVirtualBackgroundItem> imageItems) {
            this.context = context;
            this.imageItems = imageItems;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {
            ZoomVideoSDKVirtualBackgroundItem imageItem = imageItems.get(position);
            int screenWidth = vbRecycleView.getWidth();
            int itemWidth = screenWidth / COLUMNS_NUM;
            holder.itemView.getLayoutParams().width = itemWidth;
            holder.itemView.getLayoutParams().height = itemWidth;
            if (imageItem != null) {
                if (imageItem.getType() == ZoomVideoSDKVirtualBackgroundDataType.ZoomVideoSDKVirtualBackgroundDataType_None) {
                    holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_ve_none));
                } else if (imageItem.getType() == ZoomVideoSDKVirtualBackgroundDataType.ZoomVideoSDKVirtualBackgroundDataType_Blur) {
                    holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_ve_blur));
                } else {
                    File imgFile = new File(imageItem.getImageFilePath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.imageView.setImageBitmap(bitmap);
                    }
                }
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vbHelper.setVirtualBackgroundItem(imageItems.get(position)) == ZoomVideoSDKErrors.Errors_Success) {
                        VBActivity.this.finish();
                    } else {
                        Toast.makeText(VBActivity.this, "the virtual background use failed", Toast.LENGTH_LONG);
                    }
                }
            });

            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ZoomVideoSDKVirtualBackgroundItem item = imageItems.get(position);
                    if (!item.canVirtualBackgroundBeDeleted()) {
                        Toast.makeText(VBActivity.this, "the virtual background can not be deleted", Toast.LENGTH_LONG);
                    } else {
                        if (vbHelper.removeVirtualBackgroundItem(item) == ZoomVideoSDKErrors.Errors_Success) {
                            Toast.makeText(VBActivity.this, "the virtual background is deleted", Toast.LENGTH_LONG);
                            finish();
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageItems.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}