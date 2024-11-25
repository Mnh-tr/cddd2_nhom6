package com.example.cddd2_nhom6.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cddd2_nhom6.adapter.BannerAdapter;
import com.example.cddd2_nhom6.adapter.DSPhimAdapter;
import com.example.cddd2_nhom6.adapter.DSPhimAdapterOphim;
import com.example.cddd2_nhom6.adapter.DSPhimTimKiemAdapter;
import com.example.cddd2_nhom6.adapter.MyExpandableListAdapter;
import com.example.cddd2_nhom6.adapter.PhimAdapter;
import com.example.cddd2_nhom6.adapter.PhimBoAdapter;
import com.example.cddd2_nhom6.adapter.PhimHoatHinhAdapter;
import com.example.cddd2_nhom6.adapter.PhimLeAdapter;
import com.example.cddd2_nhom6.adapter.QuocGiaAdapter;
import com.example.cddd2_nhom6.adapter.QuocGiaLocAdapter;
import com.example.cddd2_nhom6.adapter.TVShowAdapter;
import com.example.cddd2_nhom6.adapter.TheLoaiAdapter;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityMainBinding;
import com.example.cddd2_nhom6.model.ApiModel;
import com.example.cddd2_nhom6.model.DSPhim;
import com.example.cddd2_nhom6.model.DSPhimAPiOphim;
import com.example.cddd2_nhom6.model.LichSuThanhToan;
import com.example.cddd2_nhom6.model.Phim;
import com.example.cddd2_nhom6.model.QLPhim;
import com.example.cddd2_nhom6.model.ThongBaoKhiUngDungTat;
import com.example.cddd2_nhom6.model.ThongBaoTrenManHinh;
import com.example.cddd2_nhom6.model.TruyCap;
import com.example.cddd2_nhom6.response.DSPhimResponse;
import com.example.cddd2_nhom6.response.DSResponseOphim;
import com.example.cddd2_nhom6.response.PhimResponse;
import com.example.cddd2_nhom6.response.PhimResponseOphim;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PhimAdapter movieAdapter;
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String idUser;
    private  String nameUser;
    private String emailUser;
    private int idLoaiND;
    // bien de kiểm tra người dùng có đang ỏ trong ứng dụng hay không
    public static Boolean truycap = false;
    private List<DSPhim> DSKkphimPhimLe, DSKkphimBo, DSKkphimHoatHinh, DSKkphimTvShow;
    private List<DSPhimAPiOphim> DSOphimLe, DSOphimBo, DSOphimHoatHinh, DSOphimTvShow;
    private Handler bannerHandler = new Handler();
    private Runnable bannerRunnable;
    private MyExpandableListAdapter adapter;
    private List<String> listHeaders;
    private HashMap<String, List<String>> listChildren;
    private Map<String, String> theLoaiSlugMap = new HashMap<>();
    private Map<String, String> quocGiaSlugMap = new HashMap<>();
    private String theLoaiSlug = null;
    private String quocGiaSlug = null;
    private boolean doubleBackToExitPressedOnce = false;
    private String selectedTheLoaiName;
    private String selectedQuocGiaName;
    private PhimAdapter phimAdapter;
    private DSPhimTimKiemAdapter dsPhimTimKiemAdapter;
    private DatabaseReference movieRef;
    private List<QLPhim> movieList;
    DatabaseReference usersRef,lichSuThanhToanRef;
    private boolean isUserLoggedIn = false; // Biến để theo dõi trạng thái đăng nhập
    private TVShowAdapter tvShowAdapter;
    private PhimBoAdapter phimBoAdapter;
    private PhimLeAdapter phimLeAdapter;
    private PhimHoatHinhAdapter phimHoatHinhAdapter;
    private TheLoaiAdapter themLoaiAdapter;
    private QuocGiaLocAdapter quocagiaAdapter;
    private int currentBannerIndex = 0;
    private static RewardedInterstitialAd quangCao;
    private static DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lichSuThanhToanRef = FirebaseDatabase.getInstance().getReference("LichSuThanhToan");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        // Khởi tạo Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> {});


        //Goi chuc nang nhan 2 lan de thoat
        getOnBackPressedDispatcher().addCallback(this, callback);
        laythongtinUser();

        theoDoiThayDoiTrenFirebase();
        // kiểm tra người dùng hết hạn gói vip chưa
         // ID Firebase của người dùng hiện tại
        if(idUser != null){
            String firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            checkAndUpdateLoaiNDForCurrentUser(firebaseUserId);
        }


        loc();
        Toast.makeText(MainActivity.this, "Xin chào " + nameUser, Toast.LENGTH_SHORT).show();
        updateUser();
        // Kiểm tra và thêm thông tin truy cập
        // neu truycap == false thì sẽ thêm vào TruyCap trên firebase
        if (truycap == false){
            // themTruyCaps(idUser);
            logUserSession(idUser);
            if(idLoaiND == 1 || idLoaiND == 2 || idLoaiND == 3){
                // không làm gì cả, vip và admin sẽ không có quảng cáo
            }else{
                // Tải quảng cáo interstitial
                taiQuangCaoAdmob();
            }

            truycap = true;
        }
        // Thiết lập ActionBar và DrawerLayout
        setSupportActionBar(binding.toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerlayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        binding.drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        // Đặt biểu tượng trở về
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiện biểu tượng trở về
        }
        // Ẩn tiêu đề
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        swipeRefreshLayout = binding.swipeRefreshLayout; // Khởi tạo SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadSeries(); // Tải lại danh sach phim bo
            loadTVShow();// Tải lại danh sách tvshow
            loadPhimLe();
            loadHoatHinh();
            hienThiBanner();
            fetchMoviesFromFirebase();
            binding.dsPhim.setVisibility(View.GONE);
            binding.recyclerViewMovies.setVisibility(View.GONE);
            binding.recyclerTimKiem.setVisibility(View.GONE);
            binding.dsPhimTimKiem.setVisibility(View.GONE);
        });
        binding.expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            String headerTitle = listHeaders.get(groupPosition);

            if ("Đăng Nhập".equals(headerTitle)) {
                Intent intent = new Intent(MainActivity.this, DangNhapActivity.class);
                startActivity(intent);
                logUserTimeout("Khach");
                truycap = false;
                return true; // Ngăn chặn mở rộng nhóm
            } else if ("Thông tin cá nhân".equals(headerTitle)) {
                Intent intent = new Intent(MainActivity.this, CaNhanActivity.class);
                startActivity(intent);
                return true; // Ngăn chặn mở rộng nhóm
            }else if ("Admin".equals(headerTitle)) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                return true; // Ngăn chặn mở rộng nhóm
            }else if ("Đăng Xuất".equals(headerTitle)) {
                dangXuat();
                return true; // Ngăn chặn mở rộng nhóm
            }

            return false; // Cho phép mở rộng nhóm nếu không phải là "Đăng Nhập" hoặc "Thông tin cá nhân"
        });

        // Lấy instance của Firebase Realtime Database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference truyCapsRef = db.getReference("TruyCaps");

        // Tạo dữ liệu ban đầu
        Map<String, Object> truyCapsData = new HashMap<>();
        truyCapsData.put("Tong", 0); // Giá trị ban đầu của tổng số lượt truy cập
        truyCapsData.put("LichSu", new ArrayList<>()); // Lịch sử ban đầu là một mảng rỗng

        // Đặt dữ liệu vào node "TruyCaps"
        truyCapsRef.setValue(truyCapsData)
                .addOnSuccessListener(aVoid -> {
                    // Thành công
                    System.out.println("TruyCaps node created successfully in Realtime Database.");
                })
                .addOnFailureListener(e -> {
                    // Thất bại
                    System.err.println("Error creating TruyCaps node: " + e.getMessage());
                });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        movieRef = database.getReference("movies"); // Đây là nơi lưu trữ thông tin phim trên Firebase

        // Khởi tạo movieList và RecyclerView
        movieList = new ArrayList<>();
        phimAdapter = new PhimAdapter(this, movieList);
        binding.recyclerViewphim.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewphim.setAdapter(phimAdapter);
        // Load phim từ Firebase
        fetchMoviesFromFirebase();
        // Khởi tạo danh sách phim
        DSKkphimPhimLe = new ArrayList<>();
        DSKkphimBo = new ArrayList<>();
        DSKkphimHoatHinh = new ArrayList<>();
        DSKkphimTvShow = new ArrayList<>();
        // Khởi tạo danh sách phim
        DSOphimLe = new ArrayList<>();
        DSOphimBo = new ArrayList<>();
        DSOphimHoatHinh = new ArrayList<>();
        DSOphimTvShow = new ArrayList<>();

        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                // Chuyển đến màn hình XemThemPhim khi người dùng nhấn nút
                binding.xemThemPhimLe.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
                    intent.putExtra("type", "phimle");
                    intent.putParcelableArrayListExtra("apiSources", new ArrayList<>(apiSources)); // Truyền danh sách API
                    startActivity(intent);
                });
                // Chuyển đến màn hình XemThemPhim khi người dùng nhấn nút
                binding.xemThemPhimBo.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
                    intent.putExtra("type", "phimbo");
                    intent.putParcelableArrayListExtra("apiSources", new ArrayList<>(apiSources)); // Truyền danh sách API
                    startActivity(intent);
                });
                // Chuyển đến màn hình XemThemPhim khi người dùng nhấn nút
                binding.xemThemHoatHinh.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
                    intent.putExtra("type", "phimhoathinh");
                    intent.putParcelableArrayListExtra("apiSources", new ArrayList<>(apiSources)); // Truyền danh sách API
                    startActivity(intent);
                });
                // Chuyển đến màn hình XemThemPhim khi người dùng nhấn nút
                binding.xemThemTVshow.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
                    intent.putExtra("type", "tvshow");
                    intent.putParcelableArrayListExtra("apiSources", new ArrayList<>(apiSources)); // Truyền danh sách API
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);


        setupRecyclerViews();
        loadSeries();
        loadTVShow();
        loadPhimLe();
        loadHoatHinh();
        hienThiBanner();
        navigationBottom();
        ghiLaiTrangThai();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Khởi tạo ThongBaoTrenManHinh
            ThongBaoTrenManHinh thongBao = new ThongBaoTrenManHinh(getApplicationContext());
            // Bắt đầu dịch vụ để lắng nghe thông báo
            thongBao.batDichVuThongBao();
            // Lấy ID người dùng và bắt đầu lắng nghe
            thongBao.layIdNguoiDungHienTai();
            // Khởi động dịch vụ khi nhận được thông báo
            Intent serviceIntent = new Intent(MainActivity.this, ThongBaoKhiUngDungTat.class);
            MainActivity.this.startForegroundService(serviceIntent);
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

        // neu truycap == false thì sẽ thêm vào TruyCap trên firebase
//        if (truycap == false){
//            themTruyCaps(idUser);
//            truycap = true;
//        }
        // Khởi tạo và chạy banner
        loaDuLieuApiKhiThayDoi();


    }
    private void laythongtinUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);
        nameUser = sharedPreferences.getString("name", null);
        emailUser  = sharedPreferences.getString("email", null);
        idLoaiND = sharedPreferences.getInt("id_loaiND", -1);
        Log.d("id_loaiND Ban đầu", String.valueOf(idLoaiND));
    }
    private void theoDoiThayDoiTrenFirebase() {
        if (idUser != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

            // Tìm bản ghi có id_user khớp với idUser
            usersRef.orderByChild("id_user").equalTo(idUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            // Lấy dữ liệu mới từ Firebase
                            String newName = userSnapshot.child("name").getValue(String.class);
                            String newEmail = userSnapshot.child("email").getValue(String.class);
                            Integer newIdLoaiND = userSnapshot.child("id_loaiND").getValue(Integer.class);

                            if (newIdLoaiND != null) {
                                // Cập nhật lại SharedPreferences với dữ liệu mới
                                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name", newName);
                                editor.putString("email", newEmail);
                                editor.putInt("id_loaiND", newIdLoaiND);
                                editor.apply();

                                // Cập nhật biến trong Activity nếu cần thiết
                                nameUser = newName;
                                emailUser = newEmail;
                                idLoaiND = newIdLoaiND;

                                Log.d("id_loaiND sau khi cập nhập", String.valueOf(idLoaiND));
                            } else {
                                Log.e("Lỗi", "Giá trị id_loaiND null");
                            }
                        }
                    } else {
                        Log.e("Lỗi", "Không tìm thấy người dùng với id_user: " + idUser);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                    Log.e("FirebaseError", "Không thể lắng nghe thay đổi dữ liệu.", error.toException());
                }
            });
        }
    }



    private void loc(){
        listHeaders = new ArrayList<>();
        listChildren = new HashMap<>();
        hienThiThongTinBoLoc();
        adapter = new MyExpandableListAdapter(this, listHeaders, listChildren);
        binding.expandableListView.setAdapter(adapter);
        binding.expandableListView.setGroupIndicator(null);
        binding.expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String header = (String) adapter.getGroup(groupPosition);
            String selectedItem = (String) adapter.getChild(groupPosition, childPosition);
            // Cập nhật tên thể loại hoặc quốc gia đã chọn
            if ("Thể Loại".equals(header)) {
                selectedTheLoaiName = selectedItem; // Lưu tên thể loại
                theLoaiSlug = theLoaiSlugMap.get(selectedItem); // Lấy slug cho thể loại
                // Cập nhật TextView hiển thị tên thể loại
                binding.tvSlug.setText("Thể loại: " + selectedTheLoaiName);
            } else if ("Quốc Gia".equals(header)) {
                selectedQuocGiaName = selectedItem; // Lưu tên quốc gia
                quocGiaSlug = quocGiaSlugMap.get(selectedItem); // Lấy slug cho quốc gia
                // Cập nhật TextView hiển thị tên quốc gia
                binding.tvSlug.setText("Quốc gia: " + selectedQuocGiaName);
            }

            if (theLoaiSlug != null) {
                hienThiPhim(theLoaiSlug, null); // Hoặc xử lý theo cách bạn muốn
                ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
                    @Override
                    public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                        // Chuyển đến màn hình XemThemPhim khi người dùng nhấn nút
                        binding.xemThemBoLoc.setOnClickListener(theloai -> {
                            Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
                            intent.putExtra("theloai", theLoaiSlug); // Thêm loại phim bộ
                            intent.putParcelableArrayListExtra("apiSources", new ArrayList<>(apiSources)); // Truyền danh sách API
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }, MainActivity.this);
            } else if (quocGiaSlug != null) {
                hienThiPhim(null, quocGiaSlug); // Hoặc xử lý theo cách bạn muốn
                ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
                    @Override
                    public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                        // Chuyển đến màn hình XemThemPhim khi người dùng nhấn nút
                        binding.xemThemBoLoc.setOnClickListener(quocgia -> {
                            Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
                            intent.putExtra("quocgia", quocGiaSlug); // Thêm loại phim bộ
                            intent.putParcelableArrayListExtra("apiSources", new ArrayList<>(apiSources)); // Truyền danh sách API
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }, MainActivity.this);

            }
            return false;
        });

    }

    private void hienThiThongTinBoLoc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // Khởi tạo danh sách con
        List<String> theLoaiItems = new ArrayList<>();
        List<String> quocGiaItems = new ArrayList<>();

        // Thêm các tiêu đề một lần duy nhất
        listHeaders.add("Thể Loại");
        listHeaders.add("Quốc Gia");
        listHeaders.add("Thông tin cá nhân");
        listHeaders.add("Admin");
        listHeaders.add("Đăng Nhập");
        if (idUser != null) {
            if (idLoaiND == 3 || idLoaiND == 2) { // Kiểm tra nếu là admin
                isUserLoggedIn = true; // Người dùng là admin

                // Kiểm tra và thêm mục "Admin" nếu chưa có
                if (!listHeaders.contains("Admin")) {
                    listHeaders.add("Admin");
                }

                // Kiểm tra và thêm mục "Đăng Xuất" nếu chưa có
                if (!listHeaders.contains("Đăng Xuất")) {
                    listHeaders.add("Đăng Xuất");
                }

                // Xóa mục "Đăng Nhập" nếu tồn tại
                listHeaders.remove("Đăng Nhập");

            } else { // Người dùng thông thường
                isUserLoggedIn = true;

                // Xóa mục "Admin" nếu tồn tại
                listHeaders.remove("Admin");

                // Kiểm tra và thêm mục "Đăng Xuất" nếu chưa có
                if (!listHeaders.contains("Đăng Xuất")) {
                    listHeaders.add("Đăng Xuất");
                }

                // Xóa mục "Đăng Nhập" nếu tồn tại
                listHeaders.remove("Đăng Nhập");
            }
        } else { // Người dùng chưa đăng nhập
            isUserLoggedIn = false;

            // Xóa các mục "Admin" và "Đăng Xuất" nếu tồn tại
            listHeaders.remove("Admin");
            listHeaders.remove("Đăng Xuất");

            // Kiểm tra và thêm mục "Đăng Nhập" nếu chưa có
            if (!listHeaders.contains("Đăng Nhập")) {
                listHeaders.add("Đăng Nhập");
            }
        }



        // Lấy dữ liệu "Thể Loại" từ Firebase
        database.child("theLoai").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                theLoaiItems.clear();
                theLoaiSlugMap.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String categoryName = itemSnapshot.child("name").getValue(String.class);
                    if (categoryName != null) {
                        String slug = convertToSlug(categoryName); // Chuyển đổi tên sang slug
                        theLoaiItems.add(categoryName);
                        theLoaiSlugMap.put(categoryName, slug); // Lưu slug theo tên thể loại
                    }
                }
                listChildren.put("Thể Loại", theLoaiItems);

                checkAndNotifyAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

        // Lấy dữ liệu "Quốc Gia" từ Firebase
        database.child("quocGia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quocGiaItems.clear(); // Xóa dữ liệu cũ nếu cần
                quocGiaSlugMap.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String countryName = itemSnapshot.child("name").getValue(String.class);
                    if (countryName != null) {
                        String slug = convertToSlug(countryName); // Chuyển đổi tên sang slug
                        quocGiaItems.add(countryName);
                        quocGiaSlugMap.put(countryName, slug); // Lưu slug theo tên thể loại
                    }
                }
                listChildren.put("Quốc Gia", quocGiaItems);

                checkAndNotifyAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void dangXuat() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("status");

            // Đặt trạng thái là "offline" trong Firebase Database
            userStatusRef.setValue("offline").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Thực hiện đăng xuất khỏi Firebase Auth
                    FirebaseAuth.getInstance().signOut();

                    // Xóa thông tin trong SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Xóa tất cả dữ liệu trong SharedPreferences
                    editor.apply();

                    // Chuyển người dùng về MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    logUserTimeout(idUser);
                    truycap = false;

                    Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Đăng xuất thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void hienThiPhim(String theLoaiSlug, String quocGiaSlug) {
        int page = 1; // Hoặc thay đổi theo nhu cầu của bạn
        // Kiểm tra xem cả hai slug đều không null
        if (theLoaiSlug != null) {
            // Danh sách để lưu kết quả của các API
            List<DSPhim> kkphimList = new ArrayList<>();
            List<DSPhim> ophimList = new ArrayList<>();
            final AtomicInteger completedApis = new AtomicInteger(0);  // Đếm số API đã hoàn thành

            // Lấy danh sách API từ Firebase
            ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {

                @Override
                public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                    for (ApiModel api : apiSources) {
                        // Tạo ApiService mới với URL tương ứng
                        ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                        if ("Kkphim".equals(api.getName())) {
                            // Gọi API của Kkphim
                            currentApiService.getTheLoaiKKPhim(theLoaiSlug,page).enqueue(new Callback<DSPhimResponse>() {
                                @Override
                                public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        List<DSPhim> kkphim = response.body().getData().getItems();
                                        if (kkphim != null) {
                                            kkphim = kkphim.subList(0, Math.min(kkphim.size(), 6));
                                            for (DSPhim phim : kkphim) {
                                                phim.setSource("Kkphim");
                                            }
                                            kkphimList.addAll(kkphim);
                                        }
                                    }
                                    checkAndUpdateRecyclerViewTheLoai(completedApis, apiSources.size(), kkphimList, ophimList);
                                }

                                @Override
                                public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                    checkAndUpdateRecyclerViewTheLoai(completedApis, apiSources.size(), kkphimList, ophimList);
                                }
                            });
                        } else if ("Ophim".equals(api.getName())) {
                            // Gọi API của Ophim
                            currentApiService.getTheLoaiOPhim(theLoaiSlug,page).enqueue(new Callback<DSPhimResponse>() {
                                @Override
                                public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        List<DSPhim> ophim = response.body().getData().getItems();
                                        if (ophim.isEmpty()) {
                                            Toast.makeText(MainActivity.this, "Không tìm thấy dữ liệu cho quốc gia này.", Toast.LENGTH_SHORT).show();
                                            binding.dsPhim.setVisibility(View.GONE);
                                            binding.recyclerViewMovies.setVisibility(View.GONE);
                                            binding.recyclerTimKiem.setVisibility(View.GONE);
                                            binding.dsPhimTimKiem.setVisibility(View.GONE);
                                            return;
                                        }
                                        if (ophim != null) {
                                            ophim = ophim.subList(0, Math.min(ophim.size(), 6));
                                            for (DSPhim phim : ophim) {
                                                phim.setSource("Ophim");
                                            }
                                            ophimList.addAll(ophim);
                                        }
                                        binding.dsPhim.setVisibility(View.VISIBLE);
                                        binding.recyclerViewMovies.setVisibility(View.VISIBLE);
                                        binding.recyclerTimKiem.setVisibility(View.GONE);
                                        binding.dsPhimTimKiem.setVisibility(View.GONE);
                                    }
                                    checkAndUpdateRecyclerViewTheLoai(completedApis, apiSources.size(), kkphimList, ophimList);
                                }

                                @Override
                                public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                    checkAndUpdateRecyclerViewTheLoai(completedApis, apiSources.size(), kkphimList, ophimList);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    binding.dsPhim.setVisibility(View.GONE);
                    binding.dsPhimTimKiem.setVisibility(View.GONE);
                    binding.recyclerViewMovies.setVisibility(View.GONE);
                    binding.recyclerTimKiem.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }, MainActivity.this);
        }else if(quocGiaSlug != null){
            // Danh sách để lưu kết quả của các API
            List<DSPhim> kkphimList = new ArrayList<>();
            List<DSPhim> ophimList = new ArrayList<>();
            final AtomicInteger completedApis = new AtomicInteger(0);  // Đếm số API đã hoàn thành

            // Lấy danh sách API từ Firebase
            ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {

                @Override
                public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                    for (ApiModel api : apiSources) {
                        // Tạo ApiService mới với URL tương ứng
                        ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                        if ("Kkphim".equals(api.getName())) {
                            // Gọi API của Kkphim
                            currentApiService.getQuocGiaKKPhim(quocGiaSlug,page).enqueue(new Callback<DSPhimResponse>() {
                                @Override
                                public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        List<DSPhim> kkphim = response.body().getData().getItems();
                                        if (kkphim != null) {
                                            kkphim = kkphim.subList(0, Math.min(kkphim.size(), 6));
                                            for (DSPhim phim : kkphim) {
                                                phim.setSource("Kkphim");
                                            }
                                            kkphimList.addAll(kkphim);
                                        }
                                    }
                                    checkAndUpdateRecyclerViewQuocGia(completedApis, apiSources.size(), kkphimList, ophimList);
                                }

                                @Override
                                public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                    checkAndUpdateRecyclerViewQuocGia(completedApis, apiSources.size(), kkphimList, ophimList);
                                }
                            });
                        } else if ("Ophim".equals(api.getName())) {
                            // Gọi API của Ophim
                            currentApiService.getQuocGiaOPhim(quocGiaSlug,page).enqueue(new Callback<DSPhimResponse>() {
                                @Override
                                public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        List<DSPhim> ophim = response.body().getData().getItems();
                                        if (ophim.isEmpty()) {
                                            Toast.makeText(MainActivity.this, "Không tìm thấy dữ liệu cho quốc gia này.", Toast.LENGTH_SHORT).show();
                                            binding.dsPhim.setVisibility(View.GONE);
                                            binding.recyclerViewMovies.setVisibility(View.GONE);
                                            binding.recyclerTimKiem.setVisibility(View.GONE);
                                            binding.dsPhimTimKiem.setVisibility(View.GONE);
                                            return;
                                        }
                                        if (ophim != null) {
                                            ophim = ophim.subList(0, Math.min(ophim.size(), 6));
                                            for (DSPhim phim : ophim) {
                                                phim.setSource("Ophim");
                                            }
                                            ophimList.addAll(ophim);
                                        }
                                        binding.dsPhim.setVisibility(View.VISIBLE);
                                        binding.recyclerViewMovies.setVisibility(View.VISIBLE);
                                        binding.recyclerTimKiem.setVisibility(View.GONE);
                                        binding.dsPhimTimKiem.setVisibility(View.GONE);
                                    }
                                    checkAndUpdateRecyclerViewQuocGia(completedApis, apiSources.size(), kkphimList, ophimList);
                                }

                                @Override
                                public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                    checkAndUpdateRecyclerViewQuocGia(completedApis, apiSources.size(), kkphimList, ophimList);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    binding.dsPhim.setVisibility(View.GONE);
                    binding.dsPhimTimKiem.setVisibility(View.GONE);
                    binding.recyclerViewMovies.setVisibility(View.GONE);
                    binding.recyclerTimKiem.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }, MainActivity.this);
        }
    }
    // Kiểm tra nếu đã tải xong dữ liệu từ tất cả các API thì cập nhật RecyclerView
    private void checkAndUpdateRecyclerViewTheLoai(AtomicInteger completedApis, int totalApis, List<DSPhim> kkphimList, List<DSPhim> ophimList) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Kết hợp danh sách phim từ cả Kkphim và Ophim
            List<DSPhim> combinedList = new ArrayList<>();
            combinedList.addAll(kkphimList);  // Thêm phim từ Kkphim
            combinedList.addAll(ophimList);   // Thêm phim từ Ophim

            // Cập nhật RecyclerView
            updateTheLoaiRecyclerView(combinedList);
        }
    }

    // Cập nhật RecyclerView với danh sách đã kết hợp
    private void updateTheLoaiRecyclerView(List<DSPhim> seriesList) {
        if (themLoaiAdapter == null) {
            themLoaiAdapter = new TheLoaiAdapter(this);
            binding.recyclerViewMovies.setAdapter(themLoaiAdapter);
        }
        themLoaiAdapter.updateFilms(seriesList);
    }

    // Kiểm tra nếu đã tải xong dữ liệu từ tất cả các API thì cập nhật RecyclerView
    private void checkAndUpdateRecyclerViewQuocGia(AtomicInteger completedApis, int totalApis, List<DSPhim> kkphimList, List<DSPhim> ophimList) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Kết hợp danh sách phim từ cả Kkphim và Ophim
            List<DSPhim> combinedList = new ArrayList<>();
            combinedList.addAll(kkphimList);  // Thêm phim từ Kkphim
            combinedList.addAll(ophimList);   // Thêm phim từ Ophim

            // Cập nhật RecyclerView
            updateQuocGiaRecyclerView(combinedList);
        }
    }

    // Cập nhật RecyclerView với danh sách đã kết hợp
    private void updateQuocGiaRecyclerView(List<DSPhim> seriesList) {
        if (quocagiaAdapter == null) {
            quocagiaAdapter = new QuocGiaLocAdapter(this);
            binding.recyclerViewMovies.setAdapter(quocagiaAdapter);
        }
        quocagiaAdapter.updateFilms(seriesList);
    }
    private String convertToSlug(String input) {
        // Chuẩn hóa văn bản để tách dấu khỏi các ký tự cơ bản
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[Đđ]", "d")   // Thay thế Đ và đ bằng d
                .replaceAll("[^\\p{ASCII}]", "")         // Xóa dấu và các ký tự đặc biệt
                .replaceAll("[^a-zA-Z0-9\\s]", "")       // Xóa các ký tự không phải chữ-số ngoại trừ khoảng trắng
                .trim()                                  // Xóa khoảng trắng đầu và cuối chuỗi
                .replaceAll("\\s+", "-")                 // Thay thế khoảng trắng bằng dấu gạch ngang
                .toLowerCase();                          // Chuyển sang chữ thường để tạo dạng slug

        return slug;
    }

    // Phương thức kiểm tra nếu dữ liệu đã sẵn sàng và thông báo adapter
    private void checkAndNotifyAdapter() {
        // Đảm bảo danh sách tiêu đề và danh sách con đã có đủ dữ liệu
        if (listHeaders.size() >= 3 && listChildren.size() >= 3) {
            adapter.notifyDataSetChanged(); // Cập nhật adapter
        }
    }

    // Hàm log truy cập (thêm session mới với time và timeout)
    public void logUserSession(String idUser) {
        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("TruyCapss");
        String idUsers = (idUser != null && !idUser.isEmpty()) ? idUser : "Khach"; // Nếu idUser null thì dùng "Khach"

        // Lấy thời gian hiện tại
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Tham chiếu đến nút LichSu cho idUser
        DatabaseReference userHistoryRef = databaseReference.child("LichSu").child(idUsers);

        // Tạo khóa ngẫu nhiên cho phiên truy cập
        DatabaseReference sessionRef = userHistoryRef.push();

        // Tạo đối tượng session với time
        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("time", currentTime);
        sessionData.put("timeout", ""); // Để trống, sẽ cập nhật sau

        // Thêm session vào Firebase
        sessionRef.setValue(sessionData)
                .addOnSuccessListener(aVoid -> incrementTotalCount(databaseReference))
                .addOnFailureListener(e -> System.err.println("Lỗi khi ghi thời gian truy cập: " + e.getMessage()));
    }
    // Hàm log thoát ứng dụng (cập nhật timeout cho session cuối cùng)
    public static void logUserTimeout(String idUser) {
        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("TruyCapss");
        String idUsers = (idUser != null && !idUser.isEmpty()) ? idUser : "Khach"; // Nếu idUser null thì dùng "Khach"

        // Lấy thời gian hiện tại
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Tham chiếu đến nút LichSu cho idUser
        DatabaseReference userHistoryRef = databaseReference.child("LichSu").child(idUsers);

        // Lấy session cuối cùng và cập nhật timeout
        userHistoryRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sessionSnapshot : snapshot.getChildren()) {
                    sessionSnapshot.getRef().child("timeout").setValue(currentTime)
                            .addOnSuccessListener(aVoid -> System.out.println("Ghi thời gian thoát thành công!"))
                            .addOnFailureListener(e -> System.err.println("Lỗi khi ghi thời gian thoát: " + e.getMessage()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Lỗi khi truy vấn session: " + error.getMessage());
            }
        });
    }


    // Hàm tăng giá trị của trường "Tong" trong Firebase
    private void incrementTotalCount(DatabaseReference databaseReference) {
        DatabaseReference totalRef = databaseReference.child("Tong");

        totalRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentValue = currentData.getValue(Integer.class);
                if (currentValue == null) {
                    currentData.setValue(1); // Nếu chưa có giá trị, khởi tạo là 1
                } else {
                    currentData.setValue(currentValue + 1); // Tăng giá trị thêm 1
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot currentData) {
                if (databaseError != null) {
                    System.err.println("Lỗi khi cập nhật Tong: " + databaseError.getMessage());
                } else {
                    System.out.println("Cập nhật Tong thành công!");
                }
            }
        });
    }



    private void updateUser(){
        // Tham chiếu đến NavigationView
        NavigationView navigationView = findViewById(R.id.navigationView);  // Giả sử NavigationView có id là nav_view

        // Lấy header view từ NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Tham chiếu đến TextView trong header view
        TextView textView = headerView.findViewById(R.id.tvTenNguoiDung); // Thay bằng id của TextView trong layout_header

        if(nameUser != null){
            // Thay đổi nội dung TextView
            textView.setText(nameUser);
        }else{
            textView.setText("Khách");
        }
    }
    private void ghiLaiTrangThai() {
        // Lấy user hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) { // Kiểm tra user có phải là null hay không
            String userId = user.getUid();
            DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("status");

            // Theo dõi trạng thái kết nối Firebase
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        // Khi người dùng kết nối với Firebase, đặt trạng thái là "online"
                        userStatusRef.setValue("online");

                        // Khi người dùng ngắt kết nối, đặt trạng thái là "offline"
                        userStatusRef.onDisconnect().setValue("offline");
                    } else {
                        // Khi không kết nối, bạn cũng có thể cập nhật lại "offline" nếu cần
                        userStatusRef.setValue("offline");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Không thể lấy trạng thái kết nối.", error.toException());
                }
            });
        } else {
            // Nếu không có user (trạng thái khách)
            updateUser(); // Cập nhật thông tin người dùng để hiển thị trạng thái "Khách"
            Log.w("Firebase", "Trạng thái Khách");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Nạp menu
        getMenuInflater().inflate(R.menu.menu_timkiem, menu);
        // Tìm kiếm item trong menu
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Lấy menu item thông báo
        MenuItem menuItem = menu.findItem(R.id.nav_thongbao);

        View actionView = MenuItemCompat.getActionView(menuItem);
        TextView badgeTextView = actionView.findViewById(R.id.notification_badge);

        // hàm thông báo
        getUnreadNotificationCount(badgeTextView,idUser);
        if (actionView != null) {
            actionView.setOnClickListener(v -> {
                // Mở màn hình thông báo
                Intent intent = new Intent(MainActivity.this, ThongBaoActivity.class);
                startActivity(intent);
            });
        }
        // Danh sách để lưu kết quả của các API
        List<DSPhim> kkphimList = new ArrayList<>();
        List<DSPhim> ophimList = new ArrayList<>();
        final AtomicInteger completedApis = new AtomicInteger(0);  // Đếm số API đã hoàn thành

        // Lấy danh sách API từ Firebase
        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {

            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        completedApis.set(0);
                        // Làm trống danh sách
                        kkphimList.clear();
                        ophimList.clear();

                        // Cập nhật RecyclerView để xóa dữ liệu cũ ngay lập tức
                        updateTimKiemPhimRecyclerView(new ArrayList<>());
                        // Khi người dùng nhấn vào nút tìm kiếm trên bàn phím
                        Toast.makeText(MainActivity.this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();

                        for (ApiModel api : apiSources) {
                            // Tạo ApiService mới với URL tương ứng
                            ApiService currentApiService = ApiClient.createApiService(api.getUrl());
                            if ("Kkphim".equals(api.getName())) {
                                currentApiService.searchMovies(query, 10).enqueue(new Callback<DSPhimResponse>() {
                                    @Override
                                    public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            List<DSPhim> kkphim = response.body().getData().getItems();
                                            if (kkphim != null) {
                                                binding.dsPhimTimKiem.setVisibility(View.VISIBLE);
                                                binding.recyclerTimKiem.setVisibility(View.VISIBLE);
                                                binding.recyclerViewMovies.setVisibility(View.GONE);
                                                binding.dsPhim.setVisibility(View.GONE);

                                                // Lấy tối đa 5 kết quả
                                                kkphim = kkphim.subList(0, Math.min(kkphim.size(), 10));
                                                for (DSPhim phim : kkphim) {
                                                    phim.setSource("Kkphim");
                                                }
                                                kkphimList.addAll(kkphim);
                                            }

                                            // Cập nhật RecyclerView
                                            checkAndUpdateRecyclerViewTimKiemPHim(completedApis, apiSources.size(), kkphimList, ophimList,query);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                        binding.dsPhimTimKiem.setVisibility(View.GONE);
                                        binding.recyclerTimKiem.setVisibility(View.GONE);
                                        binding.recyclerViewMovies.setVisibility(View.GONE);
                                        binding.dsPhim.setVisibility(View.GONE);
                                        checkAndUpdateRecyclerViewTimKiemPHim(completedApis, apiSources.size(), kkphimList, ophimList,query);
                                    }
                                });
                            } else if ("Ophim".equals(api.getName())) {
                                currentApiService.searchMoviesOphim(query, 10).enqueue(new Callback<DSPhimResponse>() {
                                    @Override
                                    public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            List<DSPhim> Ophim = response.body().getData().getItems();
                                            if (Ophim != null && !Ophim.isEmpty()) {
                                                binding.dsPhimTimKiem.setVisibility(View.VISIBLE);
                                                binding.recyclerTimKiem.setVisibility(View.VISIBLE);
                                                binding.recyclerViewMovies.setVisibility(View.GONE);
                                                binding.dsPhim.setVisibility(View.GONE);

                                                Ophim = Ophim.subList(0, Math.min(Ophim.size(), 10));
                                                for (DSPhim phim : Ophim) {
                                                    phim.setSource("Ophim");
                                                }
                                                ophimList.addAll(Ophim);
                                            }

                                            // Cập nhật RecyclerView
                                            checkAndUpdateRecyclerViewTimKiemPHim(completedApis, apiSources.size(), kkphimList, ophimList,query);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                        binding.dsPhimTimKiem.setVisibility(View.GONE);
                                        binding.recyclerTimKiem.setVisibility(View.GONE);
                                        binding.recyclerViewMovies.setVisibility(View.GONE);
                                        binding.dsPhim.setVisibility(View.GONE);
                                        swipeRefreshLayout.setRefreshing(false); // Ngừng loading
                                        checkAndUpdateRecyclerViewTimKiemPHim(completedApis, apiSources.size(), kkphimList, ophimList,query);
                                    }
                                });
                            }

                        }

                        // Đóng SearchView sau khi tìm kiếm
                        searchView.clearFocus();
                        searchItem.collapseActionView();

                        return true;
                    }


                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

            }

            @Override
            public void onError(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
                Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);
        return true;
    }
    // Kiểm tra nếu đã tải xong dữ liệu từ tất cả các API thì cập nhật RecyclerView
    private void checkAndUpdateRecyclerViewTimKiemPHim(AtomicInteger completedApis, int totalApis, List<DSPhim> kkphimList, List<DSPhim> ophimList, String query) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Kết hợp danh sách phim từ cả Kkphim và Ophim
            List<DSPhim> combinedList = new ArrayList<>();
            combinedList.addAll(kkphimList);  // Thêm phim từ Kkphim
            combinedList.addAll(ophimList);   // Thêm phim từ Ophim

            // Kiểm tra nếu không có kết quả từ cả hai API
            if (combinedList.isEmpty()) {
                binding.dsPhimTimKiem.setVisibility(View.GONE);
                binding.recyclerTimKiem.setVisibility(View.GONE);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Kết quả")
                        .setMessage("Không tìm thấy phim \"" + query + "\"")
                        .setCancelable(false) // Không cho phép đóng khi nhấn bên ngoài
                        .create();
                alertDialog.show();
                // Tự động đóng sau 2 giây (2000ms)
                new Handler(Looper.getMainLooper()).postDelayed(alertDialog::dismiss, 2000);
            } else {
                // Hiển thị RecyclerView với kết quả tìm kiếm
                updateTimKiemPhimRecyclerView(combinedList);
            }
        }
    }


    // Cập nhật RecyclerView với danh sách đã kết hợp
    private void updateTimKiemPhimRecyclerView(List<DSPhim> seriesList) {
        if (dsPhimTimKiemAdapter == null) {
            dsPhimTimKiemAdapter = new DSPhimTimKiemAdapter(this);
            binding.recyclerTimKiem.setAdapter(dsPhimTimKiemAdapter);
        }
        dsPhimTimKiemAdapter.updateFilms(seriesList);
        dsPhimTimKiemAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            // Xử lý sự kiện khi nhấn vào tìm kiếm
            Toast.makeText(this, "Bạn muốn tìm kiếm gì", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_thongbao) {
            Intent intent = new Intent(MainActivity.this,ThongBaoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setupRecyclerViews() {
        binding.recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerTimKiem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewtvShow.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewphimle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewphimhoathinh.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewphim.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }
    private void fetchMoviesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movieList.clear(); // Xóa dữ liệu cũ (nếu có)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu từ snapshot
                    QLPhim movie = snapshot.getValue(QLPhim.class);
                    if (movie != null) {
                        // Kiểm tra và thêm vào danh sách
                        movieList.add(movie);
                    }
                }
                // Cập nhật RecyclerView sau khi có dữ liệu
                phimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to fetch data", databaseError.toException());
            }
        });
    }


    private void loadSeries() {
        int page = 1;
        // Danh sách để lưu kết quả của các API
        List<DSPhim> kkphimList = new ArrayList<>();
        List<DSPhim> ophimList = new ArrayList<>();
        final AtomicInteger completedApis = new AtomicInteger(0);  // Đếm số API đã hoàn thành

        // Lấy danh sách API từ Firebase
        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {

            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    // Tạo ApiService mới với URL tương ứng
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getSeries(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 5));
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                            phim.setType("phimbo");
                                        }
                                        kkphimList.addAll(kkphim);
                                    }
                                }
                                checkAndUpdateRecyclerViewPhimBo(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerViewPhimBo(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getSeriesOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 5));
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                            phim.setType("phimbo");
                                        }
                                        ophimList.addAll(ophim);
                                    }
                                }
                                checkAndUpdateRecyclerViewPhimBo(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerViewPhimBo(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    }
                }
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }

            @Override
            public void onError(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
                Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);
    }

    // Kiểm tra nếu đã tải xong dữ liệu từ tất cả các API thì cập nhật RecyclerView
    private void checkAndUpdateRecyclerViewPhimBo(AtomicInteger completedApis, int totalApis, List<DSPhim> kkphimList, List<DSPhim> ophimList) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Kết hợp danh sách phim từ cả Kkphim và Ophim
            List<DSPhim> combinedList = new ArrayList<>();
            combinedList.addAll(kkphimList);  // Thêm phim từ Kkphim
            combinedList.addAll(ophimList);   // Thêm phim từ Ophim

            // Cập nhật RecyclerView
            updateSeriesRecyclerView(combinedList);
        }
    }

    // Cập nhật RecyclerView với danh sách đã kết hợp
    private void updateSeriesRecyclerView(List<DSPhim> seriesList) {
        if (phimBoAdapter == null) {
            phimBoAdapter = new PhimBoAdapter(this);
            binding.recyclerViewSeries.setAdapter(phimBoAdapter);
        }
        phimBoAdapter.updateFilms(seriesList);
    }

    private void loadPhimLe() {
        int page = 1;
        // Danh sách để lưu kết quả của các API
        List<DSPhim> kkphimList = new ArrayList<>();
        List<DSPhim> ophimList = new ArrayList<>();
        final AtomicInteger completedApis = new AtomicInteger(0);  // Đếm số API đã hoàn thành

        // Lấy danh sách API từ Firebase
        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {

            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    // Tạo ApiService mới với URL tương ứng
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getPhimLe(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 5));
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                            phim.setType("phimle");
                                        }
                                        kkphimList.addAll(kkphim);
                                    }
                                }
                                checkAndUpdateRecyclerViewPhimLe(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerViewPhimLe(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getPhimLeOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 5));
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                            phim.setType("phimle");
                                        }
                                        ophimList.addAll(ophim);
                                    }
                                }
                                checkAndUpdateRecyclerViewPhimLe(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerViewPhimLe(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    }
                }
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }

            @Override
            public void onError(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
                Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);
    }

    // Kiểm tra nếu đã tải xong dữ liệu từ tất cả các API thì cập nhật RecyclerView
    private void checkAndUpdateRecyclerViewPhimLe(AtomicInteger completedApis, int totalApis, List<DSPhim> kkphimList, List<DSPhim> ophimList) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Kết hợp danh sách phim từ cả Kkphim và Ophim
            List<DSPhim> combinedList = new ArrayList<>();
            combinedList.addAll(kkphimList);  // Thêm phim từ Kkphim
            combinedList.addAll(ophimList);   // Thêm phim từ Ophim

            // Cập nhật RecyclerView
            updatePhimLeRecyclerView(combinedList);
        }
    }

    // Cập nhật RecyclerView với danh sách đã kết hợp
    private void updatePhimLeRecyclerView(List<DSPhim> seriesList) {
        if (phimLeAdapter == null) {
            phimLeAdapter = new PhimLeAdapter(this);
            binding.recyclerViewphimle.setAdapter(phimLeAdapter);
        }
        phimLeAdapter.updateFilms(seriesList);
    }


    private void loadTVShow() {
        int page = 1;
        // Danh sách để lưu kết quả của các API
        List<DSPhim> kkphimList = new ArrayList<>();
        List<DSPhim> ophimList = new ArrayList<>();
        final AtomicInteger completedApis = new AtomicInteger(0);  // Đếm số API đã hoàn thành
        // Lấy danh sách API từ Firebase
        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {

            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    // Tạo ApiService mới với URL tương ứng
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getTVShow(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 5));
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                            phim.setType("tvshow");
                                        }
                                        kkphimList.addAll(kkphim);
                                    }
                                }
                                checkAndUpdateRecyclerView(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getTVShowOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 5));
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                            phim.setType("tvshow");
                                        }
                                        ophimList.addAll(ophim);
                                    }
                                }
                                checkAndUpdateRecyclerView(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    }
                }
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }

            @Override
            public void onError(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
                Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);
    }

    // Kiểm tra nếu đã tải xong dữ liệu từ tất cả các API thì cập nhật RecyclerView
    private void checkAndUpdateRecyclerView(AtomicInteger completedApis, int totalApis, List<DSPhim> kkphimList, List<DSPhim> ophimList) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Kết hợp danh sách phim từ cả Kkphim và Ophim
            List<DSPhim> combinedList = new ArrayList<>();
            combinedList.addAll(kkphimList);  // Thêm phim từ Kkphim
            combinedList.addAll(ophimList);   // Thêm phim từ Ophim

            // Cập nhật RecyclerView
            updateTVShowRecyclerView(combinedList);
        }
    }

    // Cập nhật RecyclerView với danh sách đã kết hợp
    private void updateTVShowRecyclerView(List<DSPhim> tvShowList) {
        if (tvShowAdapter == null) {
            tvShowAdapter = new TVShowAdapter(this);
            binding.recyclerViewtvShow.setAdapter(tvShowAdapter);
        }
        tvShowAdapter.updateFilms(tvShowList);
    }





    private void loadHoatHinh() {
        int page = 1;
        // Danh sách để lưu kết quả của các API
        List<DSPhim> kkphimList = new ArrayList<>();
        List<DSPhim> ophimList = new ArrayList<>();
        final AtomicInteger completedApis = new AtomicInteger(0);  // Đếm số API đã hoàn thành
        // Lấy danh sách API từ Firebase
        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {

            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    // Tạo ApiService mới với URL tương ứng
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getHoatHinh(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 5));
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                            phim.setType("phimhoathinh");
                                        }
                                        kkphimList.addAll(kkphim);
                                    }
                                }
                                checkAndUpdateRecyclerViewHoatHinh(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerViewHoatHinh(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getHoatHinhOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 5));
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                            phim.setType("phimhoathinh");
                                        }
                                        ophimList.addAll(ophim);
                                    }
                                }
                                checkAndUpdateRecyclerViewHoatHinh(completedApis, apiSources.size(), kkphimList, ophimList);
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerViewHoatHinh(completedApis, apiSources.size(), kkphimList, ophimList);
                            }
                        });
                    }
                }
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }

            @Override
            public void onError(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
                Toast.makeText(MainActivity.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);
    }

    // Kiểm tra nếu đã tải xong dữ liệu từ tất cả các API thì cập nhật RecyclerView
    private void checkAndUpdateRecyclerViewHoatHinh(AtomicInteger completedApis, int totalApis, List<DSPhim> kkphimList, List<DSPhim> ophimList) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Kết hợp danh sách phim từ cả Kkphim và Ophim
            List<DSPhim> combinedList = new ArrayList<>();
            combinedList.addAll(kkphimList);  // Thêm phim từ Kkphim
            combinedList.addAll(ophimList);   // Thêm phim từ Ophim

            // Cập nhật RecyclerView
            updatePhimHoatHinhRecyclerView(combinedList);
        }
    }

    // Cập nhật RecyclerView với danh sách đã kết hợp
    private void updatePhimHoatHinhRecyclerView(List<DSPhim> tvShowList) {
        if (phimHoatHinhAdapter == null) {
            phimHoatHinhAdapter = new PhimHoatHinhAdapter(this);
            binding.recyclerViewphimhoathinh.setAdapter(phimHoatHinhAdapter);
        }
        phimHoatHinhAdapter.updateFilms(tvShowList);
    }


    private void navigationBottom() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Đặt item mặc định được chọn là màn hình Home
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Xử lý sự kiện chọn item của Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_home) {
                    return true;
                } else if (item.getItemId() == R.id.nav_vip) {
                    intent = new Intent(MainActivity.this, VipActivity.class);
                }else if(item.getItemId() == R.id.nav_profile) {
                    intent = new Intent(MainActivity.this, CaNhanActivity.class);
                }else if (item.getItemId() == R.id.nav_download) {
                    intent = new Intent(MainActivity.this, TaiPhimActivity.class);
                }

                // Pass the selected item to the new Activity
                if (intent != null) {
                    intent.putExtra("selected_item_id", item.getItemId());
                    startActivity(intent);
                    overridePendingTransition(0, 0);  // No animation for smooth transition
                    return true;
                }
                return false;

            }
        });
    }

    private void hienThiBanner() {
        dungBanner();
        // Lấy danh sách API từ Firebase
        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                List<Phim> combinedMovies = new ArrayList<>();
                final AtomicInteger completedApis = new AtomicInteger(0);

                for (ApiModel api : apiSources) {
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());
                    if ("Kkphim".equals(api.getName())) {
                        // Gửi yêu cầu lấy phim từ API hiện tại
                        currentApiService.getMovies(1).enqueue(new Callback<PhimResponse>() {
                            @Override
                            public void onResponse(Call<PhimResponse> call, Response<PhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<Phim> movies = response.body().getItems();
                                    Log.d("API Response", "Đã lấy được danh sách phim từ " + api.getName() + ": " + movies.size());

                                    if (movies != null) {
                                        // Lấy 5 phim mới nhất
                                        List<Phim> top5Movies = movies.subList(0, Math.min(5, movies.size()));
                                        for (Phim phim : movies) {
                                            phim.setSource("Kkphim");
                                        }
                                        combinedMovies.addAll(top5Movies);
                                    }

                                    // Kiểm tra nếu tất cả API đã hoàn thành
                                    checkAndDisplayBanner(completedApis, apiSources.size(), combinedMovies);
                                } else {
                                    Log.e("API Error", "Phản hồi không thành công từ " + api.getName());
                                    checkAndDisplayBanner(completedApis, apiSources.size(), combinedMovies);
                                }
                            }

                            @Override
                            public void onFailure(Call<PhimResponse> call, Throwable t) {
                                Log.e("API Error", "Lỗi kết nối tới " + api.getName() + ": " + t.getMessage());
                                checkAndDisplayBanner(completedApis, apiSources.size(), combinedMovies);
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gửi yêu cầu lấy phim từ API hiện tại
                        currentApiService.getMoviesOphim(1).enqueue(new Callback<PhimResponse>() {
                            @Override
                            public void onResponse(Call<PhimResponse> call, Response<PhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<Phim> movies = response.body().getItems();
                                    Log.d("API Response", "Đã lấy được danh sách phim từ " + api.getName() + ": " + movies.size());
                                    if (movies != null) {
                                        // Lấy 5 phim mới nhất
                                        List<Phim> top5Movies = movies.subList(0, Math.min(5, movies.size()));
                                        for (Phim phim : movies) {
                                            phim.setSource("Ophim");
                                        }
                                        combinedMovies.addAll(top5Movies);
                                    }

                                    // Kiểm tra nếu tất cả API đã hoàn thành
                                    checkAndDisplayBanner(completedApis, apiSources.size(), combinedMovies);
                                } else {
                                    Log.e("API Error", "Phản hồi không thành công từ " + api.getName());
                                    checkAndDisplayBanner(completedApis, apiSources.size(), combinedMovies);
                                }
                            }

                            @Override
                            public void onFailure(Call<PhimResponse> call, Throwable t) {
                                Log.e("API Error", "Lỗi kết nối tới " + api.getName() + ": " + t.getMessage());
                                checkAndDisplayBanner(completedApis, apiSources.size(), combinedMovies);
                            }
                        });
                    }
                }
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }

            @Override
            public void onError(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
                Log.e("Firebase Error", "Lỗi khi lấy danh sách API: " + errorMessage);
                Toast.makeText(MainActivity.this, "Không thể tải dữ liệu banner", Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);
    }

    // Kiểm tra nếu tất cả API đã hoàn thành, sau đó hiển thị banner
    private void checkAndDisplayBanner(AtomicInteger completedApis, int totalApis, List<Phim> combinedMovies) {
        if (completedApis.incrementAndGet() == totalApis) {
            // Chỉ lấy tối đa 10 phim từ danh sách đã kết hợp
            List<Phim> bannerMovies = combinedMovies.subList(0, Math.min(10, combinedMovies.size()));

            // Thiết lập banner với danh sách phim đã chọn
            thietLapBannerViewPager(bannerMovies);
        }
    }

    private void thietLapBannerViewPager(List<Phim> movies) {
        if (movies.isEmpty()) {
            Toast.makeText(MainActivity.this, "Không có phim để hiển thị banner", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thiết lập Adapter
        BannerAdapter bannerAdapter = new BannerAdapter(this, movies);
        binding.viewPagerBanner.setAdapter(bannerAdapter);

        // Đặt Runnable cho cuộn tự động
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (movies.isEmpty() || binding.viewPagerBanner == null) return;

                currentBannerIndex = (currentBannerIndex + 1) % movies.size();
                binding.viewPagerBanner.setCurrentItem(currentBannerIndex, true);
                bannerHandler.postDelayed(this, 3000); // Lặp lại sau 3 giây
            }
        };

        // Bắt đầu cuộn
        startAutoScroll();
    }

    private void startAutoScroll() {
        stopAutoScroll(); // Dừng cuộn trước khi khởi động lại
        bannerHandler.postDelayed(bannerRunnable, 3000); // Bắt đầu sau 3 giây
    }

    private void stopAutoScroll() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    private void dungBanner() {
        // Xóa runnable hiện tại khỏi handler nếu có
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    // Thiết lập OnBackPressedDispatcher
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();  // Thoát ứng dụng
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Nhấn thoát thêm một lần nữa", Toast.LENGTH_SHORT).show();

            // Reset lại cờ sau 2 giây
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    };
    public void getUnreadNotificationCount(TextView badgeTextView,String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ThongBao");

        databaseReference.orderByChild("id_user").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int unreadCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long idTrangThai = snapshot.child("id_TrangThai").getValue(Long.class);
                    if (idTrangThai != null && idTrangThai == 0) { // 1 là trạng thái chưa đọc
                        unreadCount++;
                    }
                }

                // Cập nhật số lượng lên giao diện
                updateNotificationBadge(badgeTextView,unreadCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }
    public void checkAndUpdateLoaiNDForCurrentUser(String firebaseUserId) {
        // Lấy ngày hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();

        // Truy vấn bảng Users để lấy id_user
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(firebaseUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy id_user của người dùng
                String idUser = dataSnapshot.child("id_user").getValue(String.class);
                if (idUser != null) {
                    // Tiến hành kiểm tra lịch sử thanh toán của id_user
                    DatabaseReference lichSuThanhToanRef = FirebaseDatabase.getInstance().getReference("LichSuThanhToan");
                    lichSuThanhToanRef.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Date latestExpirationDate = null;

                            // Duyệt qua các giao dịch để tìm ngày hết hạn mới nhất
                            for (DataSnapshot paymentSnapshot : dataSnapshot.getChildren()) {
                                String ngayHetHan = paymentSnapshot.child("ngayHetHan").getValue(String.class);
                                try {
                                    if (ngayHetHan != null) {
                                        Date expirationDate = dateFormat.parse(ngayHetHan);

                                        if (latestExpirationDate == null || (expirationDate != null && expirationDate.after(latestExpirationDate))) {
                                            latestExpirationDate = expirationDate;
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Nếu có ngày hết hạn mới nhất, kiểm tra xem đã hết hạn chưa
                            if (latestExpirationDate != null) {
                                if (latestExpirationDate.before(currentDate)) {
                                    // Ngày hết hạn đã qua, cập nhật id_loaiND của user thành 0
                                    usersRef.child(firebaseUserId).child("id_loaiND").setValue(0).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            System.out.println("Cập nhật id_loaiND = 0 cho user: " + firebaseUserId);
                                        } else {
                                            System.out.println("Lỗi khi cập nhật id_loaiND: " + task.getException().getMessage());
                                        }
                                    });
                                }
                            } else {
                                System.out.println("Không tìm thấy lịch sử thanh toán của user: " + idUser);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("Lỗi đọc lịch sử thanh toán: " + databaseError.getMessage());
                        }
                    });
                } else {
                    System.out.println("Không tìm thấy id_user cho firebaseUserId: " + firebaseUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Lỗi đọc dữ liệu từ Users: " + databaseError.getMessage());
            }
        });
    }

    private void updateNotificationBadge(TextView badgeTextView,int count) {

        if (count > 0) {
            badgeTextView.setText(String.valueOf(count));
            badgeTextView.setVisibility(View.VISIBLE);
        } else {
            badgeTextView.setVisibility(View.GONE);
        }
    }
    private void taiQuangCaoAdmob() {
        // Tải quảng cáo Interstitial
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedInterstitialAd.load(this, "ca-app-pub-3940256099942544/5354046379", adRequest,
                new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        quangCao = ad;
                        Log.d("AdMob", "Quảng cáo đã tải thành công.");

                        // Thiết lập Callback để theo dõi trạng thái
                        quangCao.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d("AdMob", "Người dùng đã đóng quảng cáo.");

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                Log.e("AdMob", "Không thể hiển thị quảng cáo: " + adError.getMessage());
                            }
                        });

                        // Hiển thị quảng cáo
                        if (quangCao != null) {
                            quangCao.show(MainActivity.this, rewardItem -> {
                                Log.d("AdMob", "Người dùng đã xem quảng cáo và nhận thưởng.");
                            });
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError adError) {
                        Log.e("AdMob", "Không thể tải quảng cáo: " + adError.getMessage());
                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Giữ màn hình sáng khi ứng dụng hoạt động
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Xóa cờ giữ màn hình sáng khi ứng dụng không còn hoạt động
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Ghi timeout khi ứng dụng thực sự bị xóa khỏi bộ nhớ
        if (isFinishing()) {
            logUserTimeout(idUser);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Kiểm tra nếu ứng dụng không thay đổi cấu hình (xoay màn hình, v.v.)
        if (!isChangingConfigurations()) {
            logUserTimeout(idUser);
        }
    }
}