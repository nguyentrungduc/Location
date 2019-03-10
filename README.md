# Location
# Geocoding (mã hóa địa lý)
- Mã hóa địa lý là quá trình chuyển đổi địa chỉ (như "1600 Amphitheater Parkway, Mountain View, CA") thành tọa độ địa lý (như vĩ độ 37,423021 và kinh độ -122,083739), mà bạn có thể sử dụng để đặt điểm đánh dấu trên bản đồ hoặc định vị bản đồ.
- Reverse geocoding  là quá trình chuyển đổi tọa độ địa lý thành địa chỉ có thể đọc được của con người.
# Google Play Services location API
## Context
- Một trong những tính tăng độc đáo của ứng dụng di động là nhận thức vị trí. Người dùng di động mang theo thiết bị của họ ở mọi nơi và thêm nhận thức về vị trí vào ứng dụng của bạn sẽ mang đến cho người dùng trải nghiệm theo ngữ cảnh nhiều hơn.Location API khả dụng trong các dịch vụ Google Play tạo điều kiện cho việc thêm nhận thức vị trí vào ứng dụng của bạn với tính năng theo dõi vị trí tự động, định vị địa lý và nhận dạng hoạt động.
## Get the last known location
- Sử dụng Google Play services location API, ứng dụng của bạn có thể yêu cầu vị trí cuối cùng của thiết bị người dùng. Trong hầu hết các trường hợp, bạn quan tâm đến vị trí hiện tại, thường tương đương với vị trí được biết đến cuối cùng của device.
- Dùng fused location provider để nhận vị trí cuối cùng của thiết bị. Fused location là một trong những location API của Google Play Service. Nó quản lý vị trí và cung cấp API để ta có thể chỉ định các request ở các mức độ chính xác khác nhau từ thấp đến cao nó cũng tỉ lệ thuận với mức độ tiêu thụ pin. Nó cũng tối ưu hóa việc sử dụng pin của thiết bị

- Để sử dụng Google Play Service : https://developers.google.com/android/guides/setup
- Ứng dụng sử dụng location service phải được cấp quyền trong manifest. Có 2 quyền cơ bản chính là ACCESS_COARSE_LOCATION và ACCESS_FINE_LOCATION. Hai quyền này xác định độ chính xác mà API trả về, nếu sử dụng ACCESS_COARSE_LOCATION -> api trả về độ chính xác tương đương với thành phố

          <manifest xmlns:android="http://schemas.android.com/apk/res/android"
              package="com.google.android.gms.location.sample.basiclocationsample" >

            <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
          </manifest>
          
### Sample 
          private lateinit var fusedLocationClient: FusedLocationProviderClient

          override fun onCreate(savedInstanceState: Bundle?) {
              // ...

              fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
              
                        fusedLocationClient.lastLocation
                  .addOnSuccessListener { location : Location? ->
                      // Got last known location. In some rare situations this can be null.
                  }
          }
- Phương thức getLastLocation() return về 1 Task mà ta có thể sử dụng để lấy về Location với kinh vĩ độ -> Nó sẽ trả về null trong các tình huống sau:
1. Location bị tắt trong setting thiết bị. Kết quả có thể bị null ngay cả khi vị trí cuối cùng được lấy trước đó vì vô hiệu hóa vị trí trong cache
2. Thiết bị không bao giờ ghi lại vị trí của nó, có thể là trường hợp của thiết bị mới hoặc thiết bị đươc setting về cài đặt gốc
3. Google Play Service trên thiết bị được restart và không có Fused Location Provider nào được hoạt động sau khi service bị restarted. Để tránh trạng thái này ta cần tạo new client và yêu cầu cập nhật vị trí
### Change location setttings
- Nếu app của bạn yêu cầu vị trí và nhận permission updates, điện thoại cần phải cài đặt hệ thống phù hợp, ví dụ như GPS hay Wifi. Thay vì trực tiếp kích hoạt các dịch vụ như GPS, ứng dụng cần chỉ định mức độ chính xác, mức tiêu thụ năng lương cần thiết và khoảng thời gian cập nhật mong muốn và thiết bị tự động thực hiện các thay đổi phù hợp với cài đặt hệ thống. Các cài đặt này được xác định bởi LocationRequest đối tượng dữ liệu.
#### Set up
- Khai báo permission

          <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                      package="com.google.android.gms.location.sample.locationupdates" >

            <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
          </manifest>
- Để lưu các tham số request cho fused location provider -> tạo 1 LocationRequest. Các tham số xác định mức độ chính xác các request vị trí. Xem các option request : https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
- Phương thức setInterval() để set rates dạng miliseconds mà app muốn update location. Lưu ý rẳng việc update vị  trí có thể nhan hơn hoặc chậm hơn rate nếu 1 ứng dụng khác đang cập nhật với tốc độ nhanh hơn hoặc chậm hơn
- setFastestInterval() - set tốc độ nhanh nhất (milisecond) mà app có thể xử lý update location. Cần set tốc độ này vì các ứng dụng khác cũng ảnh hưởng đến tốc độ gửi bản cập nhật. API Google Play Location Service gửi các bản cập nhật với tốc độ nhanh nhất mà bất kỳ ứng dụng nào đã yêu cầu setInterval(). Nếu tốc độ này nhanh hơn ứng dụng của bạn có thể xử lý, bạn có thể gặp phải sự cố với việc update UI  . Để ngăn chặn điều này, -> setFastestInterval() để đặt giới hạn trên cho tốc độ cập nhật.
- setPriority -> set độ yey tiên của request :
PRIORITY_BALANCED_POWER_ACCURACY
PRIORITY_HIGH_ACCURACY
PRIORITY_LOW_POWER
PRIORITY_NO_POWER
- Những độ ưu tiên này phù hợp với từng mục đích sử dụng với mức độ chính xác và mức độ tiêu thụ pin khác nhau 

          fun createLocationRequest() {
              val locationRequest = LocationRequest.create()?.apply {
                  interval = 10000
                  fastestInterval = 5000
                  priority = LocationRequest.PRIORITY_HIGH_ACCURACY
              }
          }
### Get current location setting

          val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        val builder = LocationSettingsRequest.Builder()
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        
### Prompt the user to change location settings
- Để xác định xem các cài đặt vị trí phù hợp với location request, thêm call back OnFailureListener để xem Task đã đúng với setting hay ko. Sau đó, kiểm tra exception và show dialog


                    task.addOnSuccessListener { locationSettingsResponse ->
              // All location settings are satisfied. The client can initialize
              // location requests here.
              // ...
          }

          task.addOnFailureListener { exception ->
              if (exception is ResolvableApiException){
                  // Location settings are not satisfied, but this can be fixed
                  // by showing the user a dialog.
                  try {
                      // Show the dialog by calling startResolutionForResult(),
                      // and check the result in onActivityResult().
                      exception.startResolutionForResult(this@MainActivity,
                              REQUEST_CHECK_SETTINGS)
                  } catch (sendEx: IntentSender.SendIntentException) {
                      // Ignore the error.
                  }
              }
          }
          
### Receive location updates
- Nếu app có thể liên tục theo dõi vị trí, nó có thể cung cấp thông tin phù hợp hơn cho người dùng.
- Ví dụ: nếu ứng dụng của bạn giúp người dùng tìm đường trong khi đi bộ hoặc lái xe, thì ứng dụng đó cần có được vị trí của thiết bị theo định kỳ. Cũng như vị trí địa lý (vĩ độ và kinh độ), bạn có thể muốn cung cấp cho người dùng thêm thông tin như hướng di chuyển, độ cao hoặc vận tốc của thiết bị. Thông tin này và hơn thế nữa, có sẵn trong Location mà ta có thể lấy no ra từ fused location provider
- Trước khi request update location, app phải kết nối với location services mà tạo location request.

          override fun onResume() {
              super.onResume()
              if (requestingLocationUpdates) startLocationUpdates()
          }

          private fun startLocationUpdates() {
              fusedLocationClient.requestLocationUpdates(locationRequest,
                      locationCallback,
                      null /* Looper */)
          }
- Call back update location: 
          
          private lateinit var locationCallback: LocationCallback
          // ...
          override fun onCreate(savedInstanceState: Bundle?) {
              // ...

              locationCallback = object : LocationCallback() {
                  override fun onLocationResult(locationResult: LocationResult?) {
                      locationResult ?: return
                      for (location in locationResult.locations){
                          // Update UI with location data
                          // ...
                      }
                  }
              }
          }
 
 - Dừng update location
 
           override fun onPause() {
              super.onPause()
              stopLocationUpdates()
          }

          private fun stopLocationUpdates() {
              fusedLocationClient.removeLocationUpdates(locationCallback)
          }
          
### Displaying a Location Address
- Khi ta lấy lastLocation() và updateLocation đêu mô tả vị trí dưới dạng location với vĩ độ và kinh độ. Để chuyển thành địa chỉ tương ứng ta sử dụng Geocoder API của Android, quá trình này gọi là Geocoding.
- Để chuyển từ kinh độ vĩ độ ra danh sách địa chỉ ta sử dụng phương thức getFromLocation(), method tốn khá nhiều thời gian nên ta cần phải sử dụng Intent Service để thực hiện nó. 
- Tại sao không nên sử dụng AsyncTask? Nó cũng là 1 cách cho chúng ta sử lý bất đồng bộ, nhưng nó đc thiết kế để tạo task chạy ngắn, asyctask không nên giữ 1 tham chiếu đến UI nếu hoạt động đc create, ví dụ như xoay thiết bị. Ngược lại Intent Service ko bị hủy khi xoay thiết bị

          <manifest xmlns:android="http://schemas.android.com/apk/res/android"
              package="com.google.android.gms.location.sample.locationaddress" >
              <application
                  ...
                  <service
                      android:name=".FetchAddressIntentService"
                      android:exported="false"/>
              </application>
              ...
          </manifest>

- Mã hóa địa ly ngược
          
          override fun onHandleIntent(intent: Intent?) {
              val geocoder = Geocoder(this, Locale.getDefault())
              // ...
          }
          
- Lấy địa chỉ đường phố từ Geocoder. để lấy kết quả quá trình geocoding, ta cần tạo constanst success hay fail 

          object Constants {
              const val SUCCESS_RESULT = 0
              const val FAILURE_RESULT = 1
              const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
              const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
              const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
              const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"
          }

- Để có đc địa chỉ đường phố tương ứng, sử dụng getFromLocation(), chuyển địa chỉ vĩ độ và kinh độ từ đối tượng ví trí và số lượng địa chỉ tối đa trả về. Bộ mã hóa địa lý trả về một list địa chỉ. Nếu không tìm thấy địa chỉ nào khớp với vị trí đã cho, nó sẽ trả về một d
list rỗng. Nếu không có dịch vụ mã hóa địa lý phụ trợ có sẵn, trình mã hóa địa lý trả về null.

          protected fun onHandleIntent(intent: Intent?) {
              intent ?: return

              var errorMessage = ""

              // Get the location passed to this service through an extra.
              val location = intent.getParcelableExtra(
                      Constants.LOCATION_DATA_EXTRA)

              // ...

              var addresses: List<Address> = emptyList()

              try {
                  addresses = geocoder.getFromLocation(
                          location.latitude,
                          location.longitude,
                          // In this sample, we get just a single address.
                          1)
              } catch (ioException: IOException) {
                  // Catch network or other I/O problems.
                  errorMessage = getString(R.string.service_not_available)
                  Log.e(TAG, errorMessage, ioException)
              } catch (illegalArgumentException: IllegalArgumentException) {
                  // Catch invalid latitude or longitude values.
                  errorMessage = getString(R.string.invalid_lat_long_used)
                  Log.e(TAG, "$errorMessage. Latitude = $location.latitude , " +
                          "Longitude =  $location.longitude", illegalArgumentException)
              }

              // Handle case where no address was found.
              if (addresses.isEmpty()) {
                  if (errorMessage.isEmpty()) {
                      errorMessage = getString(R.string.no_address_found)
                      Log.e(TAG, errorMessage)
                  }
                  deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
              } else {
                  val address = addresses[0]
                  // Fetch the address lines using getAddressLine,
                  // join them, and send them to the thread.
                  val addressFragments = with(address) {
                      (0..maxAddressLineIndex).map { getAddressLine(it) }
                  }
                  Log.i(TAG, getString(R.string.address_found))
                  deliverResultToReceiver(Constants.SUCCESS_RESULT,
                          addressFragments.joinToString(separator = "\n"))
              }
          }
          
- Start Intent Service

          class MainActivity : AppCompatActivity(), ConnectionCallbacks, OnConnectionFailedListener {

              private var lastLocation: Location? = null
              private lateinit var resultReceiver: AddressResultReceiver

              // ...
              private fun startIntentService() {

                  val intent = Intent(this, FetchAddressIntentService::class.java).apply {
                      putExtra(Constants.RECEIVER, resultReceiver)
                      putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
                  }
                  startService(intent)
              }
          }
- Nhận kết quả geocoding

          class MainActivity : AppCompatActivity() {
              // ...
              internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

                  override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

                      // Display the address string
                      // or an error message sent from the intent service.
                      addressOutput = resultData?.getString(Constants.RESULT_DATA_KEY) ?: ""
                      displayAddressOutput()

                      // Show a toast message if an address was found.
                      if (resultCode == Constants.SUCCESS_RESULT) {
                          showToast(getString(R.string.address_found))
                      }

                  }
              }
          }

### Optimize location for battery
- 

          




