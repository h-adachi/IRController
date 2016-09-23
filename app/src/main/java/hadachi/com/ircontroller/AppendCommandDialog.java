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

/**
 * Created by hide on 2016/09/23.
 */

public class AppendCommandDialog extends DialogFragment
{
    IOkButtonListener mListener;
    IDeviceID mDeviceID;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_append_command, null);

        final CommandInfo bundleci = (CommandInfo)getArguments().getSerializable(getString(R.string.intent_extra_commandinfo));
        boolean isAppend = bundleci == null;

        if(!isAppend)
        {
            EditText et = (EditText) view.findViewById(R.id.command_name);
            et.setText(bundleci.Name);
            et = (EditText) view.findViewById(R.id.command_data);
            et.setText(bundleci.Data);
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle(R.string.append_command)
                // Add action buttons
                .setPositiveButton(R.string.action_append, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface df, int id)
                    {
                        if(mListener == null) return;
                        if(mDeviceID == null) return;

                        Dialog dialog = getDialog();
                        CommandInfo ci = bundleci;
                        if(ci == null) ci = new CommandInfo();
                        ci.Device = mDeviceID.GetDeviceID();
                        EditText et = (EditText)dialog.findViewById(R.id.command_name);
                        ci.Name = et.getText().toString();
                        et = (EditText)dialog.findViewById(R.id.command_data);
                        ci.Data = et.getText().toString();
                        SQLHelper helper = new SQLHelper(getActivity());
                        helper.CommitCommand(ci);

                        mListener.onOkClicked();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface df, int id)
                    {
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof IOkButtonListener == false) return;
        if(context instanceof IDeviceID == false) return;

        mListener = (IOkButtonListener)context;
        mDeviceID = (IDeviceID)context;
    }
}