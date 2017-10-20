/**
 * Created by hsaraowgi on 10/13/17.
 */
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;


public class Delete
{
    public static void main(String[] args)
            throws Exception
    {
        if(args[1].toLowerCase().equals("all"))
        {
            int number= DeleteAllJobs(args[0],"dev2",args[2]);
            System.out.println(number+" Jobs with KeyWord Deleted on dev2");
            number= DeleteAllJobs(args[0],"test",args[2]);
            System.out.println(number+" Jobs with KeyWord Deleted on test");
            number= DeleteAllJobs(args[0],"staging",args[2]);
            System.out.println(number+" Jobs with KeyWord Deleted on staging");
        }
        else {
            int number = DeleteAllJobs(args[0], args[1], args[2]);
            System.out.println(number + " Jobs with KeyWord Deleted on " + args[1]);
        }
    }

    private static int DeleteAllJobs(String Keyword,String env,String key)
            throws Exception
    {
        if(key!=null)
            System.setProperty("AccessKey", key);
        else
            System.setProperty("AccessKey", "59fd1316b88f5515df40d817ff728119");

        if(env.toLowerCase().equals("staging"))
            System.setProperty("TEST_URL", "http://discovery-staging.core.cvent.org/ix-service-staging");
        else if(env.toLowerCase().equals("test"))
            System.setProperty("TEST_URL", "http://discovery-alpha.core.cvent.org/ix-service-test");
        else if(env.toLowerCase().equals("dev2"))
            System.setProperty("TEST_URL", "http://discovery-alpha.core.cvent.org/ix-service-dev2");
        else
            System.setProperty("TEST_URL", env);

        int ticketscount = 0;
        int deletedjobs=0;
        int loop = 0;

        URL url = new URL(System.getProperty("TEST_URL") + "/jobs");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "api_key "+System.getProperty("AccessKey"));
        conn.setDoOutput(true);
        int resp = conn.getResponseCode();
        if (resp != 200) {
            throw new RuntimeException(" HTTP error code : " +
                            resp);
        }
        Scanner scan = new Scanner(conn.getInputStream());
        String entireResponse = new String();
        while (scan.hasNext()) {
                    entireResponse = entireResponse + scan.nextLine();
        }
        scan.close();
        conn.disconnect();
        int strlen= entireResponse.length();
        entireResponse=entireResponse.substring(1);
        entireResponse="{alljobs:["+entireResponse+"}";
        JSONObject json = new JSONObject(entireResponse);
        ticketscount = json.getJSONArray("alljobs").length();
        //json.getJSONArray("alljobs").getJSONObject(0).getString("id");
        for (loop = 0; loop < ticketscount; loop++) {
            String keyToCheck=json.getJSONArray("alljobs").getJSONObject(loop).toString();
            if((keyToCheck.contains(Keyword)) && (keyToCheck.contains("ats")==false) && (keyToCheck.contains("cca")==false) && (keyToCheck.contains("emtEventURL")==false))
            {
                url = new URL(System.getProperty("TEST_URL") + "/jobs/"+json.getJSONArray("alljobs").getJSONObject(loop).getString("id")+"/cancel");
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "api_key "+System.getProperty("AccessKey"));
                conn.setDoOutput(true);
                resp = conn.getResponseCode();
                if (resp != 200) {
                    throw new RuntimeException(" HTTP error code : " +
                            resp);
                }
                deletedjobs++;
            }


        }
        conn.disconnect();
        return deletedjobs;
    }

        }






