package audi.mmi.launcher;

import android.content.Context;
import android.os.Handler;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

public class UARTPort
{
    private Context DeviceUARTContext;

    private Runnable m_readRunnable = null;

    byte[] m_readDataByte = new byte[0];

    private D2xxManager m_managerDevice;
    private FT_Device ftDev = null;

    private int m_devCount = 0;
    private int m_currentIndex = -1;
    private int m_openIndex = 0;

    private String m_textLog;

    static int m_isEnableRead = 1;

    /*local variables*/ int m_baudRate = 115200; /*baud rate*/
    byte m_stopBit = 1; /*1:1stop bits, 2:2 stop bits*/
    byte m_dataBit = 8; /*8:8bit, 7: 7bit*/
    byte m_parity = 0;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    byte m_flowControl = 0; /*0:none, 1: flow control(CTS,RTS)*/
    int m_portNumber = 1; /*port number*/

    private static final int m_readLength = 512;
    private int m_readCount = 0;
    private int m_isAvailable = 0;
    private byte[] m_readData;
    private boolean m_isStartReadThread = false;
    private ReadThread m_readThread;
    private boolean m_isUartConfigured = false;

    public boolean GetIsUartConfigured()
    {
        return m_isUartConfigured;
    }

    public static Boolean GetIsEnableRead()
    {
        return m_isEnableRead == 1;
    }

    public byte[] GetReadDataByte()
    {
        return m_readDataByte;
    }

    public String GetTextLog()
    {
        return m_textLog;
    }


    public UARTPort()
    {
    }

    public void SetReadRunnable(Runnable runnable)
    {
        this.m_readRunnable = runnable;
        ReadData();
    }

    public Boolean ConnectToManager(Context context)
    {
        DeviceUARTContext = context;
        try
        {
            m_managerDevice = D2xxManager.getInstance(DeviceUARTContext);
        } catch(D2xxManager.D2xxException ex)
        {
            m_textLog = ex.toString();
            return false;
        }
        m_readData = new byte[m_readLength];
        return true;
    }

    public void Connect()
    {
        m_devCount = 0;
        CreateDeviceList();
        if(m_devCount > 0)
        {
            ConnectFunction();
            SetConfig(m_baudRate, m_dataBit, m_stopBit, m_parity, m_flowControl);
        }
    }

    public Boolean WriteData(byte[] OutData)
    {
        if(!CheckDeviceToWrite()) return false;
        SendMessage(OutData);
        return true;
    }

    public boolean ReadData()
    {
        if(!CheckDevice()) return false;

        EnableRead();
        return GetIsEnableRead();
    }

    private void EnableRead()
    {
        m_isEnableRead = (m_isEnableRead + 1) % 2;

        if(m_isEnableRead == 1)
        {
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX));
            ftDev.restartInTask();
        } else
        {
            ftDev.stopInTask();
        }
    }

    private void SendMessage(byte[] OutData)
    {
        ftDev.setLatencyTimer((byte) 16);
        ftDev.write(OutData, OutData.length);
    }

    private boolean CheckDeviceToWrite()
    {
        if(!CheckDevice()) return false;

        if(ftDev.isOpen() == false)
        {
            m_textLog = "SendMessage: device not open";
            return false;
        }
        return true;
    }

    private boolean CheckDevice()
    {
        if(m_devCount <= 0 || ftDev == null)
        {
            m_textLog = "Device not open yet...";
            return false;
        }

        if(m_isUartConfigured == false)
        {
            m_textLog = "UART not configure yet...";
            return false;
        }
        return true;
    }


    private void CreateDeviceList()
    {
        int tempDevCount = m_managerDevice.createDeviceInfoList(DeviceUARTContext);
        if(tempDevCount > 0)
        {
            if(m_devCount != tempDevCount) m_devCount = tempDevCount;
        } else
        {
            m_devCount = -1;
            m_currentIndex = -1;
        }
    }

    public void DisconnectFunction()
    {
        m_devCount = -1;
        m_currentIndex = -1;
        m_isStartReadThread = false;
        try
        {
            Thread.sleep(50);
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        if(ftDev != null)
        {
            synchronized(ftDev)
            {
                if(true == ftDev.isOpen())
                {
                    ftDev.close();
                }
            }
        }
    }

    private void ConnectFunction()
    {
        int tmpProtNumber = m_openIndex + 1;

        if(m_currentIndex != m_openIndex)
        {
            if(null == ftDev)
            {
                ftDev = m_managerDevice.openByIndex(DeviceUARTContext, m_openIndex);
            } else
            {
                synchronized(ftDev)
                {
                    ftDev = m_managerDevice.openByIndex(DeviceUARTContext, m_openIndex);
                }
            }
            m_isUartConfigured = false;
        } else
        {
            m_textLog = ("Device port " + tmpProtNumber + " is already opened");
            return;
        }

        if(ftDev == null)
        {
            m_textLog = ("open device port(" + tmpProtNumber + ") NG, ftDev == null");
            return;
        }

        if(true == ftDev.isOpen())
        {
            m_currentIndex = m_openIndex;
            m_textLog = ("open device port(" + tmpProtNumber + ") OK");

            if(false == m_isStartReadThread)
            {
                m_readThread = new ReadThread(new Handler());
                m_readThread.start();
                m_isStartReadThread = true;
            }
        } else
        {
            m_textLog = "open device port(" + tmpProtNumber + ") NG";
        }
    }

    private void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl)
    {
        if(!ftDev.isOpen())
        {
            m_textLog = "SetConfig: device not open";
            return;
        }
        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch(dataBits)
        {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch(stopBits)
        {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch(parity)
        {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch(flowControl)
        {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);

        m_isUartConfigured = true;
        m_textLog = "Config done";
    }

    private class ReadThread extends Thread
    {
        Handler m_handler;

        ReadThread(Handler h)
        {
            m_handler = h;
            this.setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run()
        {
            while(true == m_isStartReadThread)
            {
                try
                {
                    Thread.sleep(50);
                } catch(InterruptedException e)
                {
                }

                synchronized(ftDev)
                {
                    m_isAvailable = ftDev.getQueueStatus();
                    if(m_isAvailable > 0)
                    {
                        if(m_isAvailable > m_readLength) m_isAvailable = m_readLength;

                        ftDev.read(m_readData, m_isAvailable);

                        m_readDataByte = new byte[m_isAvailable];

                        for(int i = 0; i < m_isAvailable; i++)
                            m_readDataByte[i] = m_readData[i];

                        m_handler.post(m_readRunnable);
                    }
                }
            }
        }
    }
}