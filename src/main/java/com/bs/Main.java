package com.bs;

import net.servicestack.func.Function;
import net.servicestack.func.Group;
import net.servicestack.func.Tuple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.servicestack.func.Func.*;

class Main {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    public static void main(String[] args) throws IOException, ParseException {
        // write your code here
        List srcList = getSrcList();
        new StatsFrame(srcList);
        System.out.println(srcList.size());
    }

    private static List getSrcList() throws IOException, ParseException {
        List fileList = fileToList();
        List list = orderByAll(fileList, Comparator.comparing(BusData::getLineCode), Comparator.comparing
                (BusData::getDirection), Comparator.comparing(BusData::getStationNum), Comparator.comparing
                (BusData::getStationCode), Comparator.comparing(BusData::getVehCode), Comparator.comparing
                (BusData::getProcessTime));
        List srcList = new ArrayList<BusData>();
        List temp = new ArrayList<BusData>();
        while (list.size() > 0) {
            for (Object data : list) {
                BusData last = (BusData) last(temp);
                if (last == null) {
                    temp.add(data);
                    continue;
                }
                long diff = (((BusData) data).getProcessTime().getTime() - last.getProcessTime().getTime()) / (1000 *
                        60);
                diff = diff < 0 ? diff + 1440 : diff;
                if (diff > 30)
                    break;
                temp.add(data);
            }
            if (temp.size() > 1) {
                BusData first = (BusData) first(temp);
                BusDataStats stats = new BusDataStats();
                stats.setLineCode(first.getLineCode());
                stats.setDirection(first.getDirection());
                stats.setStationNum(first.getStationNum());
                stats.setStationCode(first.getStationCode());
                stats.setVehCode(first.getVehCode());
                stats.setFirstEstimatedTime(first.getEstimatedTime());
                stats.setFirstProcessTime(first.getProcessTime());
                stats.setLastEstimatedTime(((BusData) last(temp)).getEstimatedTime());
                stats.setLastProcessTime(((BusData) last(temp)).getProcessTime());
                if (!((stats.getTimeDiffMin()) < -30))
                    srcList.add(stats);
            }
            list = skip(list, temp.size());
            temp.clear();
        }
        return srcList;
    }

    /**
     * @return Bus data list from txt file
     * @throws IOException    IOException
     * @throws ParseException ParseException
     */
    private static List fileToList() throws IOException, ParseException {
        List res = new ArrayList<BusData>();
//        BufferedReader br = new BufferedReader(new FileReader(Main.class.getResource("").getPath() + "\\data.txt"));
//        BufferedReader br=new BufferedReader(new InputStreamReader(Main.class.getClass().getResourceAsStream("data
// .txt")));
        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\data.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] arr = line.split("\\|");
            if (arr.length == 0 || arr.length < 8 || arr[6].isEmpty() ||
                    arr[7].isEmpty()) continue;
            BusData data = new BusData();
            data.setLineCode(arr[1]);
            data.setDirection(Direction.values()[Integer.parseInt(arr[2])]);
            data.setStationNum(Integer.parseInt(arr[3]));
            data.setStationCode(arr[4]);
            data.setVehCode(arr[5]);
            data.setEstimatedTime(dateFormat.parse(arr[6]));
            data.setProcessTime(dateFormat.parse(arr[7]));
//            data.ProcessTime.setSeconds(0);
//            Calendar cal=Calendar.getInstance(Locale.CHINA);
//            cal.setTime(data.ProcessTime);
//            cal.set(Calendar.SECOND,0);
//            data.ProcessTime=cal.getTime();
            res.add(data);
        }
        br.close();
        return res;
    }
}




