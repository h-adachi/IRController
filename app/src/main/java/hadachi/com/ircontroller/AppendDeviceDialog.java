package hadachi.com.ircontroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AppendDeviceDialog extends DialogFragment
{
    IOkButtonListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_append_device, null);

        final DeviceInfo bundledi = (DeviceInfo)getArguments().getSerializable(getString(R.string.intent_extra_deviceinfo));
        boolean isAppend = bundledi == null;
        if(!isAppend)
        {
            EditText et = (EditText) view.findViewById(R.id.device_name);
            et.setText(bundledi.Name);
            et = (EditText) view.findViewById(R.id.device_customer);
            et.setText(bundledi.Costomer);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle(isAppend ? R.string.append_device : R.string.edit_device)
                // Add action buttons
                .setPositiveButton(isAppend ? R.string.action_append : R.string.action_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface df, int id) {
                        if (mListener == null) return;

                        Dialog dialog = getDialog();
                        DeviceInfo di = bundledi;
                        if(di == null) di = new DeviceInfo();
                        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.device_carrier_type);
                        RadioButton rb = (RadioButton) dialog.findViewById(rg.getCheckedRadioButtonId());
                        di.Type = rb.getText().toString();
                        EditText et = (EditText) dialog.findViewById(R.id.device_name);
                        di.Name = et.getText().toString();
                        et = (EditText) dialog.findViewById(R.id.device_customer);
                        di.Costomer = et.getText().toString();
                        SQLHelper helper = new SQLHelper(getActivity());
                        helper.CommitDevice(di);

                        mListener.onOkClicked();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface df, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof IOkButtonListener == false) return;

        mListener = (IOkButtonListener)context;
    }
}