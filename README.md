# Location
### Google Play Services location API
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




