package h_adachi.com.ircontroller;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommitCommandDialog extends DialogFragment implements DialogInterface.OnClickListener
{
    IOkButtonListener mListener;
    DeviceInfo mDeviceInfo;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        CommandInfo ci = (CommandInfo)getArguments().getSerializable(getString(R.string.intent_extra_command_info));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_commit_command_dialog, null);
        builder.setView(view);
        builder.setNegativeButton(R.string.dialog_result_cancel, null);
        if(ci == null) Create(builder, view);
        else Edit(builder, view, ci);
        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof IOkButtonListener == false) return;
        if(context instanceof IDeviceInfo == false) return;

        mListener = (IOkButtonListener)context;
        mDeviceInfo = ((IDeviceInfo)context).GetDeviceInfo();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i)
    {
        Dialog dialog = getDialog();
        CommandInfo ci = (CommandInfo)getArguments().getSerializable(getString(R.string.intent_extra_command_info));
        if(ci == null) ci = new CommandInfo();
        ci.Device = mDeviceInfo.id;
        EditText et = (EditText) dialog.findViewById(R.id.dialog_command_name);
        ci.Name = et.getText().toString();
        et = (EditText) dialog.findViewById(R.id.dialog_command_data);
        ci.Data = et.getText().toString();
        SQLHelper helper = new SQLHelper(getActivity());
        helper.CommitCommand(ci);

        mListener.onOkClicked();
    }

    private void Create(AlertDialog.Builder builder, View view)
    {
        builder.setTitle(R.string.dialog_title_create);
        builder.setPositiveButton(R.string.dialog_result_ok, this);
    }

    private void Edit(AlertDialog.Builder builder, View view,CommandInfo ci)
    {
        builder.setTitle(R.string.dialog_title_edit);
        builder.setPositiveButton(R.string.dialog_result_ok, this);

        EditText et = (EditText) view.findViewById(R.id.dialog_command_name);
        et.setText(ci.Name);
        et = (EditText) view.findViewById(R.id.dialog_command_data);
        et.setText(ci.Data);
    }
}
