package hadachi.com.ircontroller;

import java.util.ArrayList;

/**
 * Created by hide on 2016/09/22.
 */

abstract class IRFormatBase
{
    // キャリア周波数
    abstract public int CarrierFrequency();

    // リーダーコード設定.
    abstract protected void LeaderCode(ArrayList<Integer> signal);

    // カスタムコード設定.
    abstract protected void CustomCode(ArrayList<Integer> signal, short custom);

    // データ設定.
    abstract protected void DataCode(ArrayList<Integer> signal, byte[] datas);

    // ビット設定.
    abstract protected void On(ArrayList<Integer> signal);
    abstract protected void Off(ArrayList<Integer> signal);

    // リピート.
    abstract public boolean IsRepeat();
    abstract public int[] Repeat();


    private ArrayList<Integer> mSignal = new ArrayList<Integer>();



    public void Init(short custom)
    {
        LeaderCode(mSignal);
        CustomCode(mSignal, custom);
    }

    // データコード設定.
    public int[] MakeData(byte[] datas)
    {
        ArrayList<Integer> dataList = new ArrayList<Integer>();
        DataCode(dataList, datas);
        TrailerCode(dataList);

        int[] result = new int[mSignal.size() + dataList.size()];
        int counter = 0;
        for(int i = 0; i < mSignal.size(); i++)
        {
            result[counter++] = mSignal.get(i);
        }
        for(int i = 0; i < dataList.size(); i++)
        {
            result[counter++] = dataList.get(i);
        }

        return result;
    }

    // トレイラー設定
    protected void TrailerCode(ArrayList<Integer> signal)
    {
        // 何もしない.
    }


    protected void AppendBit(ArrayList<Integer> signal, int num, int bits)
    {
        for(int i = 0; i < bits; i++)
        {
            if((num & 1) == 1) On(signal);
            else Off(signal);

            num >>= 1;
        }
    }
}
