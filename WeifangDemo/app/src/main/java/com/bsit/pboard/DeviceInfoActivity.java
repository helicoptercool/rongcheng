package com.bsit.pboard;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bsit.pboard.R;
import java.io.IOException;

public class DeviceInfoActivity extends Activity {

    private static final String LOG_TAG = "DeviceInfoActivity";
    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String FILENAME_PROC_BASEBAND_VERSION = "/proc/modem_version";
    private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";

    private ImageView backIv;
    private TextView modelNumberTv;
    private TextView firmwareVersionTv;
    private TextView romInfoTv;
    private TextView ramInfoTv;
    private TextView cpuInfoTv;
    private TextView basebandVersionTv;
    private TextView kernelVersionTv;
    private TextView buildNumberTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        backIv = (ImageView)findViewById(R.id.back_iv);
        modelNumberTv = (TextView) findViewById(R.id.model_number_tv);
        firmwareVersionTv = (TextView) findViewById(R.id.firmware_version_tv);
        romInfoTv = (TextView) findViewById(R.id.rom_info_tv);
        ramInfoTv = (TextView) findViewById(R.id.ram_info_tv);
        cpuInfoTv = (TextView) findViewById(R.id.cpu_info_tv);
        basebandVersionTv = (TextView) findViewById(R.id.baseband_version_tv);
        kernelVersionTv = (TextView) findViewById(R.id.kernel_version_tv);
        buildNumberTv = (TextView) findViewById(R.id.build_number_tv);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        modelNumberTv.setText(Build.DISPLAY);
        firmwareVersionTv.setText(Build.VERSION.RELEASE);
        romInfoTv.setText(getRomInfo());
        ramInfoTv.setText(getRamInfo());
        cpuInfoTv.setText(getCpuInfo());
        basebandVersionTv.setText(getBasebandVersion());
        kernelVersionTv.setText(getFormattedKernelVersion());
        buildNumberTv.setText(Build.MODEL + getMsvSuffix());
    }

     /**
     * Reads a line from the specified file.
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private static String readLine(String filename) throws IOException {
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

        } catch (java.io.IOException e) {
            android.util.Log.e(LOG_TAG,
                "IO Exception when getting kernel version for Device Info screen",
                e);

            return "Unavailable";
        }
    }

    public static String getBasebandVersion() {
        try {
            StringBuilder baseband = new StringBuilder();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(FILENAME_PROC_BASEBAND_VERSION), 256);
            String line = null;
            while ((line = reader.readLine()) != null) {
                baseband.append(line);
                baseband.append("\n");
            }
            return baseband.toString();

        } catch (java.io.IOException e) {
            android.util.Log.e(LOG_TAG,
                "IO Exception when getting baseband version for Device Info screen",
                e);

            return "Unavailable";
        }
    }

    public static String getRomInfo() {
        try {
            StringBuilder rominfo = new StringBuilder();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("/proc/mtd"), 256);
            String line = null;
            long rom_size =0;
            long userdata_size = 0;
            long sd_size=0;
            boolean hasExtra=false;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String parts[] = line.split(" ");
                long myIntNumber = Long.parseLong(parts[1], 16);
                rom_size += myIntNumber;
                if (hasExtra) {
                    sd_size += myIntNumber;
                }else if (parts[3].equals("\"userdata\"")) {
                    userdata_size += myIntNumber;
                    hasExtra = true;
                }
                android.util.Log.d("MemInfo","getRomInfo part="+parts[3]+",size="+parts[1]+",myIntNumber="+myIntNumber);
            }

            if (userdata_size == 0) {
                android.os.StatFs sf = new android.os.StatFs(android.os.Environment.getDataDirectory().getPath());
                long blockSize = sf.getBlockSize();
                long totalCount = sf.getBlockCount();
                userdata_size = (totalCount * blockSize);
                android.util.Log.d("MemInfo","userdata_size = " + userdata_size);
            }

            rominfo.append("Internal rom: "+(rom_size/1024/1024)+"MB");
            rominfo.append("\n");
            rominfo.append("--System: "+((rom_size-userdata_size -sd_size)/1024/1024)+"MB");
            rominfo.append("\n");
            rominfo.append("--User available: "+(userdata_size/1024/1024)+"MB");
            if (sd_size !=0) {
                rominfo.append("\n");
                rominfo.append("--Internal SD: "+(sd_size/1024/1024)+"MB");
            }
            return rominfo.toString();

        } catch (java.io.IOException e) {
            android.util.Log.e(LOG_TAG,
                "IO Exception when getting Rom info for Device Info screen",
                e);

            return "Unavailable";
        }
    }

    public static String getRamInfo() {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("/proc/meminfo"), 256);
            String line = null;
            int size =0;
            while ((line = reader.readLine()) != null) {
                String ram = line.trim();
                ram = ram.substring(9,ram.length()-2);
                ram = ram.trim();
                android.util.Log.d("MemInfo","getRamInfo line="+line+",ram.length"+ram.length()+",ram="+ram);
                int ram_size = Integer.parseInt(ram, 10);
                ram_size = ram_size/1024;
                int fact = ram_size/256;
                size = 256*(fact+1);
                android.util.Log.d("MemInfo","getRamInfo ram_size="+ram_size+"MB,ret size="+size+"MB");
                return Integer.toString(size)+"MB";
            }
        } catch (java.io.IOException e) {
            android.util.Log.e(LOG_TAG,
                "IO Exception when getting Rom info for Device Info screen",
                e);
        }
        return "Unavailable";
    }

    public static String getCpuInfo() {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("/sys/devices/system/cpu/present"), 256);
            String line = null;
            double size =0.0;
            int count=0;
            while ((line = reader.readLine()) != null) {
                android.util.Log.d("MemInfo","getCpuInfo line="+line);
                if (line.length() >=3 && line.charAt(1) == '-')
                    count = line.charAt(2)-'0';
                else
                    count = line.charAt(0)-'0';
                android.util.Log.d("MemInfo","getCpuInfo count="+count+",line.charAt(0)="+line.charAt(0));
                break;
            }
            reader = new java.io.BufferedReader(new java.io.FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"), 256);
            while ((line = reader.readLine()) != null) {
                android.util.Log.d("MemInfo","getCpuInfo 2 line="+line);
                size = Double.parseDouble(line);
                size = size/1000000;
                android.util.Log.d("MemInfo","getCpuInfo size="+size+"GHz");
                break;
            }
            String[] corecount={"Single","Dual","","Quad"};
            if(count > 3){
                count = 3;
            }
            return corecount[count]+"-Core "+size+"GHz";
        } catch (java.io.IOException e) {
            android.util.Log.e(LOG_TAG,
                "IO Exception when getting Rom info for Device Info screen",
                e);
        }
        return "Unavailable";
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        final String PROC_VERSION_REGEX =
            "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
            "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
            "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
            "(#\\d+) " +              /* group 3: "#1" */
            "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
            "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        java.util.regex.Matcher m = java.util.regex.Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            android.util.Log.e(LOG_TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            android.util.Log.e(LOG_TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
            m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
            m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

    /**
     * Returns " (ENGINEERING)" if the msv file has a zero value, else returns "".
     * @return a string to append to the model number description.
     */
    private String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it, assume it's a
        // production device so that we don't accidentally show that it's an ENGINEERING device.
        try {
            String msv = readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (java.io.IOException ioe) {
            // Fail quietly, as the file may not exist on some devices.
        } catch (NumberFormatException nfe) {
            // Fail quietly, returning empty string should be sufficient
        }
        return "";
    }

}
