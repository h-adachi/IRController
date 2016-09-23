package hadachi.com.ircontroller;

import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;

public class TargetSelectActivity extends AppCompatActivity implements IOkButtonListener
{
    DeviceInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConsumerIrManager ir = (ConsumerIrManager)getSystemService(CONSUMER_IR_SERVICE);
        if(!ir.hasIrEmitter())
        {
            //finish();
        }
        ListView lv = (ListView)findViewById(R.id.device_list);
        registerForContextMenu(lv);

        UpdateDeviceList();
        setTitle(getString(R.string.select_device));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_target_select, menu);
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

            AppendDeviceDialog dialog = new AppendDeviceDialog();
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
        ListView lv = (ListView)findViewById(R.id.device_list);
        DeviceInfo di = (DeviceInfo)lv.getAdapter().getItem(info.position);
        switch (item.getItemId())
        {
            case R.id.context_edit:
                AppendDeviceDialog dialog = new AppendDeviceDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.intent_extra_deviceinfo), (Serializable) di);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "Dialog");
                return true;

            case R.id.context_delete:
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
        SQLHelper sql = new SQLHelper(this);
        if(sql.GetDevice() == null) return;

        ListView lv = (ListView)findViewById(R.id.device_list);
        mAdapter = new DeviceInfoAdapter(this, R.layout.device_list, sql.GetDevice());
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ListView list = (ListView)parent;
                DeviceInfo di = (DeviceInfo)list.getItemAtPosition(position);

                Intent intent = new Intent(list.getContext(), CommandSelectActivity.class);
                intent.putExtra(getString(R.string.intent_extra_deviceinfo), (Serializable) di);
                startActivity(intent);
            }
        });
    }
}
