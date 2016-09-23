package hadachi.com.ircontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hide on 2016/09/23.
 */

public class DeviceInfoAdapter extends ArrayAdapter<DeviceInfo>
{
    private LayoutInflater mInflater;
    private int mResource;

    public DeviceInfoAdapter(Context context, int resource, List<DeviceInfo> objects)
    {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = mInflater.inflate(mResource, null);
        }

        DeviceInfo di = getItem(position);
        TextView tv = (TextView)convertView.findViewById(R.id.device_info_type);
        tv.setText(di.Type);
        tv = (TextView)convertView.findViewById(R.id.device_info_name);
        tv.setText(di.Name);

        return convertView;
    }
}
