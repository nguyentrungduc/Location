# Location
### Google Play Services location API
## Context
- Một trong những tính tăng độc đáo của ứng dụng di động là nhận thức vị trí. Người dùng di động mang theo thiết bị của họ ở mọi nơi và thêm nhận thức về vị trí vào ứng dụng của bạn sẽ mang đến cho người dùng trải nghiệm theo ngữ cảnh nhiều hơn.Location API khả dụng trong các dịch vụ Google Play tạo điều kiện cho việc thêm nhận thức vị trí vào ứng dụng của bạn với tính năng theo dõi vị trí tự động, định vị địa lý và nhận dạng hoạt động.
## Get the last known location
- Sử dụng Google Play services location API, ứng dụng của bạn có thể yêu cầu vị trí cuối cùng của thiết bị người dùng. Trong hầu hết các trường hợp, bạn quan tâm đến vị trí hiện tại, thường tương đương với vị trí được biết đến cuối cùng của device.
- Dùng fused location provider để nhận vị trí cuối cùng của thiết bị. Fused location là một trong những location API của Google Play Service. Nó quản lý vị trí và cung cấp API để ta có thể chỉ định các request ở các mức độ chính xác khác nhau từ thấp đến cao nó cũng tỉ lệ thuận với mức độ tiêu thụ pin. Nó cũng tối ưu hóa việc sử dụng pin của thiết bị

          public void getLastLocation() {
              // Get last known recent location using new Google Play Services SDK (v11+)
              FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

              locationClient.getLastLocation()
                          .addOnSuccessListener(new OnSuccessListener<Location>() {
                              @Override
                              public void onSuccess(Location location) {
                                  // GPS location can be null if GPS is switched off
                                  if (location != null) {
                                      onLocationChanged(location);
                                  }
                              }
                          })
                          .addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Log.d("MapDemoActivity", "Error trying to get last GPS location");
                                  e.printStackTrace();
                              }
                          });
          }
          
- Để sử dụng Google Play Service : https://developers.google.com/android/guides/setup
- Ứng dụng sử dụng location service phải 
          



