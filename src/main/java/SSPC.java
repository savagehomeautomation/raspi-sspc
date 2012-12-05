import com.savagehomeautomation.raspi.sspc.SunriseSunsetPowerController;

/**
 * This class acts as a lightweight shim to 
 * launch the power controller implementation.
 *  
 * @author Robert Savage
 * @see http://www.savagehomeautomation.com/projects/raspberry-pi-sunrise-sunset-timer-for-christmas-lights.html
 */
public class SSPC
{
    public static void main(String[] args)
    {
        // create controller instance and start it up
        SunriseSunsetPowerController sspc = new SunriseSunsetPowerController();
        sspc.start(args);
    }
}
