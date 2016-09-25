package h_adachi.com.ircontroller;

import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class CommandChoiceActivity extends AppCompatActivity implements IOkButtonListener, IDeviceInfo
{
    ConsumerIrManager mIR;
    DeviceInfo mDeviceInfo;
    CommandInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_choice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIR = (ConsumerIrManager)getSystemService(CONSUMER_IR_SERVICE);
        mDeviceInfo = (DeviceInfo)getIntent().getSerializableExtra(getString(R.string.intent_extra_device_info));

        ListView lv = (ListView)findViewById(R.id.command_choice_listview);
        registerForContextMenu(lv);

        UpdateCommandList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_command_choice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_action_append)
        {

            CommitCommandDialog dialog = new CommitCommandDialog();
            Bundle bundle = new Bundle();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "Dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle(R.string.menu_operation);
        getMenuInflater().inflate(R.menu.menu_choice_operation, menu);
    }

    @Override public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        ListView lv = (ListView)findViewById(R.id.command_choice_listview);
        CommandInfo ci = (CommandInfo)lv.getAdapter().getItem(info.position);
        switch (item.getItemId())
        {
            case R.id.menu_operation_edit:
                CommitCommandDialog dialog = new CommitCommandDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.intent_extra_command_info), (Serializable) ci);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "Dialog");
                return true;

            case R.id.menu_operation_delete:
                SQLHelper sql = new SQLHelper(this);
                sql.DeleteCommand(ci.id);
                UpdateCommandList();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public DeviceInfo GetDeviceInfo()
    {
        return mDeviceInfo;
    }

    @Override
    public void onOkClicked()
    {
        UpdateCommandList();
    }

    private void UpdateCommandList()
    {
        ListView lv = (ListView)findViewById(R.id.command_choice_listview);
        SQLHelper sql = new SQLHelper(this);

        ArrayList<CommandInfo> list = sql.GetCommand(mDeviceInfo.id);
        if(list == null)
        {
            if(mAdapter != null)
            {
                mAdapter.clear();
            }
            return;
        }
        mAdapter = new CommandInfoAdapter(this, R.layout.listview_command_choice, sql.GetCommand(mDeviceInfo.id));
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ListView list = (ListView)parent;
                CommandInfo ci = (CommandInfo)list.getItemAtPosition(position);

                IRFormatBase ir = null;
                switch(mDeviceInfo.Format)
                {
                    // NEC
                    case R.id.dialog_device_format_nec:
                        ir = new IRFormatNEC();
                        ir.Init((short)Integer.parseInt(mDeviceInfo.Customer, 16));
                        break;
                }
                if(ir == null) return;

                String[] datas = ci.Data.split(",");
                ArrayList<Byte> dataList = new ArrayList<Byte>();
                for(int i = 0; i < datas.length; i++)
                {
                    if(datas[i].equals("R"))
                    {
                        if(dataList.size() > 0)
                        {
                            mIR.transmit(ir.CarrierFrequency(), ir.MakeData(dataList));
                        }
                        if(ir.IsRepeat())
                        {
                            mIR.transmit(ir.CarrierFrequency(), ir.Repeat());
                        }
                        dataList.clear();
                    }
                    else
                    {
                        dataList.add((byte)(Integer.parseInt(datas[i], 16)));
                    }
                }
                if(dataList.size() > 0)
                {
                    mIR.transmit(ir.CarrierFrequency(), ir.MakeData(dataList));
                }
            }
        });
    }
}
