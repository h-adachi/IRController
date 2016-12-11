package h_adachi.com.ircontroller;

import java.util.ArrayList;

/**
 * Created by hide on 2016/12/11.
 */

public class IRFormatAEHA extends IRFormatBase
{
    private final int T = 425;
    @Override
    public int CarrierFrequency()
    {
        return 38000;
    }

    @Override
    protected void LeaderCode(ArrayList<Integer> signal)
    {
        signal.add(T * 8);
        signal.add(T * 4);
    }

    @Override
    protected void CustomCode(ArrayList<Integer> signal, short custom)
    {
        AppendBit(signal, custom, 16);
    }

    @Override
    protected void DataCode(ArrayList<Integer> signal, byte data)
    {
        AppendBit(signal, data, 8);
    }

    @Override
    protected void TrailerCode(ArrayList<Integer> signal)
    {
        On(signal);
    }

    @Override
    protected void On(ArrayList<Integer> signal)
    {
        signal.add(T * 1);
        signal.add(T * 3);
    }

    @Override
    protected void Off(ArrayList<Integer> signal)
    {
        signal.add(T * 1);
        signal.add(T * 1);
    }

    @Override
    public boolean IsRepeat()
    {
        return true;
    }

    @Override
    public int[] Repeat()
    {
        return new int[]{T * 8, T * 8};
    }
}
