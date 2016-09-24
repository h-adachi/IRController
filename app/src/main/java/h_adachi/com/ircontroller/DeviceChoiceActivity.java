package h_adachi.com.ircontroller;

import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceChoiceActivity extends AppCompatActivity implements IOkButtonListener
{
    DeviceInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConsumerIrManager ir = (ConsumerIrManager)getSystemService(CONSUMER_IR_SERVICE);
        if(!ir.hasIrEmitter())
        {
            //finish();
        }

        setTitle(getString(R.string.title_activity_device_choice));
        ListView lv = (ListView)findViewById(R.id.device_choice_listview);
        registerForContextMenu(lv);

        UpdateDeviceList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_device_choice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_append)
        {
            CommitDeviceDialog dialog = new CommitDeviceDialog();
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
        ListView lv = (ListView)findViewById(R.id.device_choice_listview);
        DeviceInfo di = (DeviceInfo)lv.getAdapter().getItem(info.position);
        switch (item.getItemId())
        {
            case R.id.operation_edit:
                CommitDeviceDialog dialog = new CommitDeviceDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.intent_extra_device_info), (Serializable) di);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "Dialog");
                return true;

            case R.id.operation_delete:
                SQLHelper sql = new SQLHelper(this);
                sql.DeleteDevice(di.id);
                UpdateDeviceList();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onOkClicked()
    {
        UpdateDeviceList();
    }

    private void UpdateDeviceList()
    {
        ListView lv = (ListView)findViewById(R.id.device_choice_listview);
        SQLHelper sql = new SQLHelper(this);
        ArrayList<DeviceInfo> list = sql.GetDevice();
        if(list == null)
        {
            if(mAdapter != null)
            {
                mAdapter.clear();
            }
            return;
        }
        mAdapter = new DeviceInfoAdapter(this, R.layout.listview_device_choice, sql.GetDevice());
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ListView list = (ListView)parent;
                DeviceInfo di = (DeviceInfo)list.getItemAtPosition(position);

                Intent intent = new Intent(list.getContext(), CommandChoiceActivity.class);
                intent.putExtra(getString(R.string.intent_extra_device_info), (Serializable) di);
                startActivity(intent);
            }
        });
    }
}
