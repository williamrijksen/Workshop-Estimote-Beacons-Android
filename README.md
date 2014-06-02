Workshop iBeacons - Android
===============
Tijdens de minor is door de groep SPWNED onderzoek gedaan naar de werking en de nauwkeurigheid van iBeacons. In deze workshop geven we jullie de eerste handreiking om met beacons aan de slag te gaan.

In deze workshop zullen we werken met Estimote beacons. In deze workshop krijg je de beschikking over één Estimote beaon. 

Opdracht
---------
Zorg ervoor dat je een Android toestel verbind met de gekregen beacon. Achterhaal de MXPower. Toon deze vervolgens in je applicatie en toon daarbij de afstand (NEAR, INMEDIATE of FAR). Daarnaast maak je een achtergrond service die een notificatie geeft zodra je in de buurt komt van je beacon.

Benodigdheden
---------
 - Android telefoon of tablet
   - Bluetooth 4.0
   - Android 4.3+  
 - Eclipse incl. Android Developer Tools geïnstalleerd
 - Het toestel moet een ontwikkeltoestel zijn *(zelf gemaakte applicaties kunnen uitvoeren)*

Stappen
---------
1. **Maak een nieuwe app aan in Eclipse**
 - New Android Application
 - Minimum SDK (API 18)
 - Verder "Next" en "Finish"
 - Doe de stappen op [Stackoverflow](http://stackoverflow.com/a/22482259) zodat fragments verwijderd worden

2. **Run de applicatie. Nu ben je ervan verzekerd dat je smartphone jouw applicatie kan runnen.**

    Nu is de template van je applicatie aangemaakt. De volgende stap is het integreren van de Estimote SDK in je project. Zorg ervoor dat je bluetooth op je toestel hebt ingeschakeld.

3. **Download de Estimote SDK en volg de installatiestappen op Github.** Integreer de Estimote SDK in de bij stap 1 aangemaakte applicatie. Om het framework te downloaden dien je op “Raw” te klikken bij de opties. Dan wordt het gehele bestand gedownload.
    -  [Estimote installatie android](https://github.com/Estimote/Android-SDK#installation)

    Als het importeren van de EstimoteSDK niet lukt, roep dan gerust 1 van de SPWNED leden bij je.
    
4. **Voeg twee textViews toe in de activity_main.xml met de volgende id’s**
    -   ```android:id="@+id/distance"```
    -   ```android:id="@+id/power"```

5. **Maak een klasse AndroidWorkshopApplication die inherit van Application**
   - *Voeg daarin het volgende veld toe*
   ```
   private BeaconManager beaconManager = null;
   ```
   - *En in de onCreate methode*
   ```
   beaconManager = new BeaconManager(this);
   ```
   - *Voeg daarnaast de volgende getters & setters toe*
   ```
   public BeaconManager getBeaconManager() {
	 if (beaconManager == null) {
        	beaconManager = new BeaconManager(this);
	    }
	    return beaconManager;
   }
   
   public void setBeaconManager(BeaconManager beaconManager) {
	    this.beaconManager = beaconManager;
   }
   ```
   - *Zorg ervoor dat in het android manifest deze Application klasse wordt herkend. Voeg in het application element het volgende toe:*
   ```
   <application android:name="com.example.androidworkshop.AndroidWorkshopApplication"></application>
   ```
    
6. **Voeg in de MainActivity een aantal zaken toe**
   - *De onderstaande velden*
   ```
   private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
   private static final String TAG = "ESPWNED";
   private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", null, null, null);
   private BeaconManager beaconManager;
   ```
   - *Onderstaande code in de onCreate methode. Voeg daarnaast in de onBeaconsDiscovered code toe die ervoor zorgt dat de textviews in de activity_main.xml worden gevuld met informatie van jouw beacon.*
   ```
   AndroidWorkshopApplication app = (AndroidWorkshopApplication) getApplication();
   beaconManager = app.getBeaconManager();
   beaconManager.setRangingListener(new BeaconManager.RangingListener() {
      @Override
      public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
         // Hiermee toon je het aantal gevonden Beacons in de actionBar
         getActionBar().setSubTitle("Beacons found" + beacons.size());
         // Haal uit List<Beacon> beacons jouw beacon en toon daarvan de power en distance (NEAR / INMEDIATE / FAR) in de     textviews 
      }
   });
   ```
  - *Onderstaande code in de onStart methode*
   ```
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
       @Override
       public void onServiceReady() {
    try {
       Log.d(TAG, "startRanging");
       beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
    } catch (RemoteException e) {
       Log.e(TAG, "Cannot start ranging", e);
            }
       }
    });
   ```
  - *In de onStop methode*
   ```
    try {
       beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
    } catch (RemoteException e) {
       Log.e(TAG, "Cannot stop but it does not matter now", e);
    }
   ```
Je hebt er in stap 5 en 6 voor gezorgd dat de beacons gevonden kunnen worden en dat de power en de afstand van jouw beacon worden getoond. Test dit door de applicatie te runnen op het android toestel *(zorg ervoor dat bluetooth is ingeschakeld)*.
**Tip: gebruik de Utils klasse van Estimote voor de afstand!**
    
7. **Nu gaan we ervoor zorgen dat de applicatie een notificatie geeft zodra je in de buurt komt van je beacon.**
  - *Voeg een klasse toe met de naam “BackgroundService.java” die inherit van de klasse IntentService*
  - *Voeg in het android manifest de volgende regel code toe. Dit zorgt ervoor dat de service gevonden kan worden.*
   ```
   <service android:name=".BackgroundService” android:exported="false"/>
   ```
  - *Maak in de BackgroundService.java de volgende velden aan*
   ```
   private static final String TAG = "ESPWNED";
   private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", null, null, null);
   private BeaconManager beaconManager;
   private String lastText = "";
   ```
  - *Voeg in de BackgroundService klasse twee lege methodes toe: onStartCommand(niet overridden) en onHandleIntent (overridden)*
  - *Voeg in de onStart methode de onderstaande code toe:*
   ```
   beaconManager.setRangingListener(new BeaconManager.RangingListener() {
       @Override
       public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
          if (beacons.size() != 0) {
             String contentText = "Power " 
                + beacons.get(0).getMeasuredPower() + " distance "
                + Utils.computeProximity(beacons.get(0)).name();
    					
             if (!lastText.equals(contentText)) {
                // maak notificatie aan
                lastText = contentText;
             }
          } else {
             NotificationManager mNotificationManager = (NotificationManager) 
                      getSystemService(Context.NOTIFICATION_SERVICE);
             mNotificationManager.cancel(1);
          }
       }
   });
   ```
  - *Voeg in de onCreate methode de volgende code toe*
   ```
    super.onCreate();
    AndroidWorkshopApplication app = (AndroidWorkshopApplication) 
    getApplication();
    beaconManager = app.getBeaconManager();
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      	@Override
      	public void onServiceReady() {
        		try {
        			  Log.d(TAG, "startRanging");
        			  beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
        		} catch (RemoteException e) {
        		  	Log.e(TAG, "Cannot start ranging", e);
        		}
      	}
    });
   ```
  - *Voeg in de onDestroy methode de volgende code toe*
   ```
    super.onDestroy();
    try {
    		beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
    } catch (RemoteException e) {
    	Log.e(TAG, "Cannot stop but it does not matter now", e);
    }
   ```
  - *Maak in de bij stap d aangemaakte onBeaconsDiscovered functie een notificatie aan waarin de power en de distance wordt getoond*
  [Android developer](http://developer.android.com/training/notify-user/build-notification.html)
  - *Zorg er in de onCreate van de MainActivity voor dat de BackgroundService wordt gestart*
  - *Test dit door de applicatie te runnen op het android toestel (zorg ervoor dat bluetooth is ingeschakeld en dat je in de buurt bent van een beacon). Als je de app start zou je een notificatie moeten krijgen van de eerste beacon.*
    
8. **Als laatste stap gaan we ervoor zorgen dat de beacons gezocht worden op het moment dat je de device opstart.** Hiervoor moet de background service worden gestart op het moment dat de smartphone wordt opgestart.  Dit gebeurd d.m.v. een zogenaamde Broadcast Receiver.
  - *Voeg in het android manifest de volgende regels toe*
   ```
   <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/> 
   ```
  - *Maak een klasse ServiceStarter aan die inherit van de klasse BroadcastReceiver. Voeg vervolgens de onReceive de volgende regel toe die ervoor zorgt dat de service wordt opgestart*
   ```
   context.startService(new Intent(context, BackgroundService.class));
   ```
  - *Voeg in het android manifest de volgende regels toe. Deze regels zorgen ervoor dat de broadcast receiver wordt gestart op het moment dat het device wordt gestart*
   ```
   <receiver android:name=".ServiceStarter">
      <intent-filter>
         <action android:name="android.intent.action.BOOT_COMPLETED"/>
         <action android:name="PACKAGE_NAME.android.action.broadcast"/>  
      </intent-filter>
   </receiver>
   ```

Applicatie testen
---------
Test de code door de applicatie opnieuw te runnen. Het testen van de code kan op twee manieren:
  - Het apparaat opnieuw op te starten. Nadeel van deze methode is dat logcat niet meer werkt nadat het apparaat opnieuw is gestart.
  - Het is mogelijk om d.m.v. een terminal commando het android device de indruk te geven dat het apparaat opnieuw wordt opgestart. Dit kan door in de terminal:
   - Naar de ADB folder te gaan (te vinden bij preferences -> android in eclipse)
   - ```cd platform-tools/```
   - Voer vervolgens het volgende commando in:
   ```./adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -c android.intent.category.HOME -n **VOLLEDIGE_PACKAGE_NAME**/.ServiceStarter```

Gefeliciteerd! Je hebt je eerste stappen gezet in de wereld van Estimote beacons. Wij hopen dat je met veel plezier deze workshop hebt gevolgd. Als je nog vragen hebt vraag het gerust bij het SPWNED team.
