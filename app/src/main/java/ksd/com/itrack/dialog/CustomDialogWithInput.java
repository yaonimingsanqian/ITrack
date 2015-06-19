package ksd.com.itrack.dialog;

/**
 * Created by kd on 2015/6/19.
 */
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import ksd.com.itrack.R;
/**
 * Created by test2 on 14/11/7.
 */
public class CustomDialogWithInput extends Dialog {
    public CustomDialogWithInput(Context context) {
        super(context);
    }

    public CustomDialogWithInput(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String positiveButtonText;
        private Button posttiveButton;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private Button close;
        public EditText myName;
        public EditText hisName;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }


        public CustomDialogWithInput create() {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialogWithInput dialog = new CustomDialogWithInput(context,R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_setkey_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            close = (Button)layout.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                    Log.v("test","close click");
                }
            });

            myName = (EditText)layout.findViewById(R.id.my);
            myName.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                }
            });

            hisName = (EditText)layout.findViewById(R.id.his);
            hisName.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                }
            });
            if (positiveButtonText != null) {
//                ((Button) layout.findViewById(R.id.positiveButton))
//                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {

                    posttiveButton = (Button)layout.findViewById(R.id.positiveButton);
                    posttiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            positiveButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                    posttiveButton.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                                Resources res = context.getResources();
                                posttiveButton.setBackground(res.getDrawable(R.drawable.kaishidown));
                            }
                            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                                Resources res = context.getResources();
                                posttiveButton.setBackground(res.getDrawable(R.drawable.kaishinormal));
                            }
                            return false;
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

    }
}
