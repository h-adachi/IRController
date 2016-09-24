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

public class CommandChoiceActivity extends AppCompatActivity implements IOkButtonListener, IDeviceID
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
        if (item.getItemId() == R.id.action_append)
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
        getMenuInflater().inflate(R.menu.menu_choice_operation, menu);
    }

    @Override public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        ListView lv = (ListView)findViewById(R.id.command_choice_listview);
        CommandInfo ci = (CommandInfo)lv.getAdapter().getItem(info.position);
        switch (item.getItemId())
        {
            case R.id.operation_edit:
                CommitCommandDialog dialog = new CommitCommandDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.intent_extra_command_info), (Serializable) ci);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "Dialog");
                return true;

            case R.id.operation_delete:
                SQLHelper sql = new SQLHelper(this);
                sql.DeleteCommand(ci.id);
                UpdateCommandList();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public int GetDeviceID()
    {
        return mDeviceInfo.id;
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
                    case 0:
                        ir = new IRFormatNEC();
                        ir.Init((short)Integer.parseInt(mDeviceInfo.Costomer, 16));
                        break;
                }
                if(ir == null) return;

                String[] dss = ci.Data.split(",");
                byte[] data = new byte[dss.length];
                for(int i = 0; i < data.length; i++)
                {
                    data[i] = (byte)Integer.parseInt(dss[i], 16);
                }
                if(mIR.hasIrEmitter())
                {
                    mIR.transmit(ir.CarrierFrequency(), ir.MakeData(data));
                }
            }
        });
    }
}
