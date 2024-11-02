package us.zoom.sdksample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;

import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKErrors;
import us.zoom.sdk.ZoomVideoSDKMaskHelper;

public class MaskActivity extends AppCompatActivity {
    private static final String TAG = "MaskActivity";


    private static final String KEY_MIRROE= "mirror";
    private static final String KEY_WIDTH = "WIDTH";
    private static final String KEY_HEIGHT = "HEIGHT";

    EditText textView1;
    EditText textView2;
    EditText textView3;
    EditText textView4;

    private final ZoomVideoSDKMaskHelper.MaskCircle circle = new ZoomVideoSDKMaskHelper.MaskCircle();
    private final ZoomVideoSDKMaskHelper.MaskRectangle rectangle = new ZoomVideoSDKMaskHelper.MaskRectangle();
    private final ZoomVideoSDKMaskHelper.MaskOval oval = new ZoomVideoSDKMaskHelper.MaskOval();

    private ZoomVideoSDKMaskHelper.MaskShape selectShape = ZoomVideoSDKMaskHelper.MaskShape.Circle;

    private boolean isMirrored;
    private int width;
    private int height;

    private final ZoomVideoSDKMaskHelper.MaskCircle circle2 = new ZoomVideoSDKMaskHelper.MaskCircle();
    private final ZoomVideoSDKMaskHelper.MaskRectangle rectangle2 = new ZoomVideoSDKMaskHelper.MaskRectangle();
    private final ZoomVideoSDKMaskHelper.MaskOval oval2 = new ZoomVideoSDKMaskHelper.MaskOval();
    private ZoomVideoSDKMaskHelper.MaskShape selectShape2 = ZoomVideoSDKMaskHelper.MaskShape.Circle;
    EditText textView21;
    EditText textView22;
    EditText textView23;
    EditText textView24;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        isMirrored = intent.getBooleanExtra(KEY_MIRROE, true);
        width = intent.getIntExtra(KEY_WIDTH, 720);
        height = intent.getIntExtra(KEY_HEIGHT, 1280);

        setContentView(R.layout.activity_mask);

        ZoomVideoSDKMaskHelper.MaskShape[] maskShapes = {ZoomVideoSDKMaskHelper.MaskShape.Circle,
                ZoomVideoSDKMaskHelper.MaskShape.Rectangle,
                ZoomVideoSDKMaskHelper.MaskShape.Oval};
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<ZoomVideoSDKMaskHelper.MaskShape> adapter = new ArrayAdapter<>(
                this, R.layout.support_simple_spinner_dropdown_item, maskShapes);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < maskShapes.length) {
                    selectShape = maskShapes[position];
                }
                updateSelect();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);


        updateSelect();

        Spinner spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<ZoomVideoSDKMaskHelper.MaskShape> adapter2 = new ArrayAdapter<>(
                this, R.layout.support_simple_spinner_dropdown_item, maskShapes);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < maskShapes.length) {
                    selectShape2 = maskShapes[position];
                }
                updateSelect2();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });

        textView21 = findViewById(R.id.textView21);
        textView22 = findViewById(R.id.textView22);
        textView23 = findViewById(R.id.textView23);
        textView24 = findViewById(R.id.textView24);
        updateSelect2();

        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoomVideoSDKMaskHelper.MaskInfo maskInfo1 = circle;
                switch (selectShape) {
                    case Oval:
                        maskInfo1 = oval;
                        break;
                    case Rectangle:
                        maskInfo1 = rectangle;
                        break;
                    default:
                        break;
                }

                ZoomVideoSDKMaskHelper.MaskInfo maskInfo2 = circle2;
                switch (selectShape2) {
                    case Oval:
                        maskInfo2 = oval2;
                        break;
                    case Rectangle:
                        maskInfo2 = rectangle2;
                        break;
                    default:
                        break;
                }

                ArrayList<ZoomVideoSDKMaskHelper.MaskInfo> maskInfos = new ArrayList<>();
                maskInfos.add(maskInfo1);
                maskInfos.add(maskInfo2);
                Bitmap mask = ZoomVideoSDK.getInstance().getMaskHelper().generateMask(maskInfos, width, height);
                if (mask == null) {
                    Log.e(TAG, "generate Mask fail");
                }

                int error = ZoomVideoSDK.getInstance().getMaskHelper().setVideoMask(
                        mask, BitmapFactory.decodeResource(getResources(), R.drawable.frame_one), isMirrored);
                if (error != ZoomVideoSDKErrors.Errors_Success) {
                    Log.e(TAG, "setVideoMask fail for error: " + error);
                }
                finish();
            }
        });
    }

    private void updateSelect() {
        String textView1Hint = "cx for circle";
        String textView2Hint = "cy for circle";
        String textView3Hint = "radius for circle";
        String textView4Hint = "";
        switch (selectShape) {
            case Oval:
                textView1Hint = "left for oval";
                textView2Hint = "top for oval";
                textView3Hint = "right for oval";
                textView4Hint = "bottom for oval";
                break;
            case Rectangle:
                textView1Hint = "left for rectangle";
                textView2Hint = "top for rectangle";
                textView3Hint = "right for rectangle";
                textView4Hint = "bottom for rectangle";
                break;
            default:
                break;
        }

        textView1.setHint(textView1Hint);
        textView2.setHint(textView2Hint);
        textView3.setHint(textView3Hint);
        textView4.setHint(textView4Hint);

        textView1.setText("");
        textView2.setText("");
        textView3.setText("");
        textView4.setText("");

        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.VISIBLE);

        findViewById(R.id.line4).setVisibility(View.VISIBLE);
        if (textView4Hint.isEmpty()) {
            textView4.setVisibility(View.GONE);
            findViewById(R.id.line4).setVisibility(View.GONE);
        }

        textView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                     floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape) {
                    case Oval:
                        oval.left = floatValue;
                        break;
                    case Rectangle:
                        rectangle.left = floatValue;
                        break;
                    default:
                        circle.cx = floatValue;
                        break;
                }
            }
        });

        textView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                    floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape) {
                    case Oval:
                        oval.top = floatValue;
                        break;
                    case Rectangle:
                        rectangle.top = floatValue;
                        break;
                    default:
                        circle.cy = floatValue;
                        break;
                }
            }
        });

        textView3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                    floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape) {
                    case Oval:
                        oval.right = floatValue;
                        break;
                    case Rectangle:
                        rectangle.right = floatValue;
                        break;
                    default:
                        circle.radius = floatValue;
                        break;
                }
            }
        });

        textView4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                    floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape) {
                    case Oval:
                        oval.bottom = floatValue;
                        break;
                    case Rectangle:
                        rectangle.bottom = floatValue;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void updateSelect2() {
        String textView1Hint = "cx for circle";
        String textView2Hint = "cy for circle";
        String textView3Hint = "radius for circle";
        String textView4Hint = "";
        switch (selectShape2) {
            case Oval:
                textView1Hint = "left for oval";
                textView2Hint = "top for oval";
                textView3Hint = "right for oval";
                textView4Hint = "bottom for oval";
                break;
            case Rectangle:
                textView1Hint = "left for rectangle";
                textView2Hint = "top for rectangle";
                textView3Hint = "right for rectangle";
                textView4Hint = "bottom for rectangle";
                break;
            default:
                break;
        }

        textView21.setHint(textView1Hint);
        textView22.setHint(textView2Hint);
        textView23.setHint(textView3Hint);
        textView24.setHint(textView4Hint);

        textView21.setText("");
        textView22.setText("");
        textView23.setText("");
        textView24.setText("");

        textView21.setVisibility(View.VISIBLE);
        textView22.setVisibility(View.VISIBLE);
        textView23.setVisibility(View.VISIBLE);
        textView24.setVisibility(View.VISIBLE);

        findViewById(R.id.line24).setVisibility(View.VISIBLE);
        if (textView4Hint.isEmpty()) {
            textView24.setVisibility(View.GONE);
            findViewById(R.id.line24).setVisibility(View.GONE);
        }

        textView21.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                    floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape2) {
                    case Oval:
                        oval2.left = floatValue;
                        break;
                    case Rectangle:
                        rectangle2.left = floatValue;
                        break;
                    default:
                        circle2.cx = floatValue;
                        break;
                }
            }
        });

        textView22.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                    floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape2) {
                    case Oval:
                        oval2.top = floatValue;
                        break;
                    case Rectangle:
                        rectangle2.top = floatValue;
                        break;
                    default:
                        circle2.cy = floatValue;
                        break;
                }
            }
        });

        textView23.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                    floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape2) {
                    case Oval:
                        oval2.right = floatValue;
                        break;
                    case Rectangle:
                        rectangle2.right = floatValue;
                        break;
                    default:
                        circle2.radius = floatValue;
                        break;
                }
            }
        });

        textView24.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float floatValue = 0;
                try {
                    floatValue = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                }
                switch (selectShape2) {
                    case Oval:
                        oval2.bottom = floatValue;
                        break;
                    case Rectangle:
                        rectangle2.bottom = floatValue;
                        break;
                    default:
                        break;
                }
            }
        });
    }


    public static void startActivity(Activity activity, boolean isMirror, int width, int height) {
        Intent intent = new Intent(activity, MaskActivity.class);
        intent.putExtra(KEY_MIRROE, isMirror);
        intent.putExtra(KEY_WIDTH, width);
        intent.putExtra(KEY_HEIGHT, height);
        activity.startActivity(intent);
    }
}
