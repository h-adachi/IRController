package h_adachi.com.ircontroller;

import java.util.ArrayList;

/**
 * Created by hide on 2016/09/22.
 */

class IRFormatNEC extends IRFormatBase
{
    private final int T = 562;
    @Override
    public int CarrierFrequency()
    {
        return 38000;
    }

    @Override
    protected void LeaderCode(ArrayList<Integer> signal)
    {
        signal.add(T * 16);
        signal.add(T * 8);
    }

    @Override
    protected void CustomCode(ArrayList<Integer> signal, short custom)
    {
        AppendBit(signal, custom, 16);
    }

    @Override
    protected void DataCode(ArrayList<Integer> signal, byte[] datas)
    {
        if(datas.length > 1) return;

        byte value = datas[0];
        AppendBit(signal, value, 8);
        value = (byte)~value;
        AppendBit(signal, value, 8);
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
        return new int[]{T * 16, T * 4};
    }
}
