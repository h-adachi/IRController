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
import android.widget.RadioGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommitDeviceDialog extends DialogFragment implements DialogInterface.OnClickListener
{
    IOkButtonListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        DeviceInfo di = (DeviceInfo)getArguments().getSerializable(getString(R.string.intent_extra_device_info));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_commit_device_dialog, null);
        builder.setView(view);
        builder.setNegativeButton(R.string.dialog_result_cancel, null);
        if(di == null) Create(builder);
        else Edit(builder, view, di);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i)
    {
        Dialog dialog = getDialog();
        DeviceInfo di = (DeviceInfo)getArguments().getSerializable(getString(R.string.intent_extra_device_info));
        if(di == null) di = new DeviceInfo();
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.dialog_device_format);
        di.Format = rg.getCheckedRadioButtonId();
        EditText et = (EditText) dialog.findViewById(R.id.dialog_device_name);
        di.Name = et.getText().toString();
        et = (EditText) dialog.findViewById(R.id.dialog_device_customer);
        di.Customer = et.getText().toString();
        SQLHelper helper = new SQLHelper(getActivity());
        helper.CommitDevice(di);

        mListener.onOkClicked();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof IOkButtonListener == false) return;

        mListener = (IOkButtonListener)context;
    }

    private void Create(AlertDialog.Builder builder)
    {
        builder.setTitle(R.string.dialog_title_create);
        builder.setPositiveButton(R.string.dialog_result_ok, this);
    }

    private void Edit(AlertDialog.Builder builder, View view,DeviceInfo di)
    {
        builder.setTitle(R.string.dialog_title_edit);
        builder.setPositiveButton(R.string.dialog_result_ok, this);

        RadioGroup rg = (RadioGroup) view.findViewById(R.id.dialog_device_format);
        rg.check(di.Format);
        EditText et = (EditText) view.findViewById(R.id.dialog_device_name);
        et.setText(di.Name);
        et = (EditText) view.findViewById(R.id.dialog_device_customer);
        et.setText(di.Customer);
    }
}
