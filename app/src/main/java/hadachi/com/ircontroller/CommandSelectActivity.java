package hadachi.com.ircontroller;

import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;

public class CommandSelectActivity extends AppCompatActivity implements IOkButtonListener, IDeviceID
{
    ConsumerIrManager mIR;
    CommandInfoAdapter mAdapter;
    DeviceInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIR = (ConsumerIrManager)getSystemService(CONSUMER_IR_SERVICE);
        mDeviceInfo = (DeviceInfo)getIntent().getSerializableExtra(getString(R.string.intent_extra_deviceinfo));

        ListView lv = (ListView)findViewById(R.id.command_list);
        registerForContextMenu(lv);

        UpdateCommandList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_command_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_append)
        {

            AppendCommandDialog dialog = new AppendCommandDialog();
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
        getMenuInflater().inflate(R.menu.menu_context, menu);
    }

    @Override public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        ListView lv = (ListView)findViewById(R.id.command_list);
        CommandInfo ci = (CommandInfo)lv.getAdapter().getItem(info.position);
        switch (item.getItemId())
        {
            case R.id.context_edit:
                AppendCommandDialog dialog = new AppendCommandDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.intent_extra_commandinfo), (Serializable) ci);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "Dialog");
                return true;

            case R.id.context_delete:
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
        SQLHelper sql = new SQLHelper(this);
        if(sql.GetCommand(mDeviceInfo.id) == null) return;

        ListView lv = (ListView)findViewById(R.id.command_list);
        mAdapter = new CommandInfoAdapter(this, R.layout.command_list, sql.GetCommand(mDeviceInfo.id));
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ListView list = (ListView)parent;
                CommandInfo ci = (CommandInfo)list.getItemAtPosition(position);

                IRFormatBase ir = null;
                switch(mDeviceInfo.Type)
                {
                    case "NEC":
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
