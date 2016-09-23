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

public class CommandInfoAdapter extends ArrayAdapter<CommandInfo>
{
    private LayoutInflater mInflater;
    private int mResource;

    public CommandInfoAdapter(Context context, int resource, List<CommandInfo> objects)
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

        CommandInfo ci = getItem(position);
        TextView tv = (TextView)convertView.findViewById(R.id.command_info_name);
        tv.setText(ci.Name);

        return convertView;
    }
}
