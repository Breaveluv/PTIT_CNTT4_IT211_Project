# BÁO CÁO CÁC BƯỚC TRIỂN KHAI (FR-04, FR-05, FR-06)

Tài liệu này giải thích chi tiết các bước kỹ thuật đã thực hiện để hoàn thành 3 chức năng cốt lõi của hệ thống.

---

## 1. FR-04: Đăng ký tài khoản Sinh viên mới (Public)

### Mục tiêu:
Cho phép người dùng tự tạo tài khoản với vai trò mặc định là `STUDENT` mà không cần thông qua Admin.

### Các bước thực hiện:
1.  **Thiết kế DTO (`RegisterRequest`)**: Tạo một đối tượng chuyển đổi dữ liệu đơn giản chỉ bao gồm `username`, `password`, và `fullName`.
2.  **Logic tại Service (`AuthService`)**:
    - Sử dụng `PasswordEncoder` (BCrypt) để mã hóa mật khẩu trước khi lưu.
    - **Quan trọng**: Hệ thống được lập trình để tự động gán `Role.STUDENT` cho mọi yêu cầu đăng ký này, đảm bảo tính an toàn (người dùng không thể tự đăng ký làm Admin).
    - Lưu vào database qua `UserRepository`.
3.  **Endpoint Controller (`AuthController`)**:
    - Expose API tại đường dẫn `/api/auth/register`.
    - Cấu hình trong `SecurityConfig` là `permitAll()` để người dùng chưa đăng nhập vẫn có thể truy cập.

---

## 2. FR-05: Quản lý Người dùng & Lớp học (Admin)

### Mục tiêu:
Cung cấp bộ công cụ CRUD (Thêm, Sửa, Xóa, Xem) mạnh mẽ cho Quản trị viên, hỗ trợ tìm kiếm và phân trang để xử lý lượng dữ liệu lớn.

### Các bước thực hiện:
1.  **Phân quyền (Authorization)**: Toàn bộ API được đặt dưới tiền tố `/api/v1/admin/**` và bảo vệ bằng Spring Security với yêu cầu `hasRole('ADMIN')`.
2.  **Triển khai Tìm kiếm & Phân trang**:
    - **Repository**: Sử dụng `@Query` và Method Name Query (ví dụ: `findByCourseNameContainingIgnoreCase`) để thực hiện tìm kiếm mờ (fuzzy search) trong database.
    - **Pagination**: Sử dụng đối tượng `Pageable` của Spring Data JPA để trả về dữ liệu theo từng trang, giúp tăng hiệu năng hệ thống.
3.  **Áp dụng Java Stream API**:
    - Trong `UserService` và `CourseService`, danh sách các thực thể (Entity) được chuyển đổi sang đối tượng phản hồi (DTO) bằng Stream API (`.stream().map().collect()`). 
    - Việc này giúp mã nguồn sạch sẽ và tuân thủ đúng yêu cầu kỹ thuật của tài liệu đặc tả (không dùng mảng truyền thống).
4.  **Standard Response Format**: Mọi kết quả đều được bọc trong đối tượng `ApiResponse` bao gồm các trường: `success`, `message`, và `data`.

---

## 3. FR-06: Đăng ký tham gia khóa học (Student)

### Mục tiêu:
Cho phép sinh viên ghi danh vào các khóa học mà Admin đã tạo.

### Các bước thực hiện:
1.  **Thiết lập quan hệ Many-to-Many**: 
    - Tạo bảng trung gian `course_students` để kết nối giữa `User` (Sinh viên) và `Course`.
2.  **Xác thực ngữ cảnh (Security Context)**:
    - **Điểm đặc biệt**: Sinh viên không cần gửi ID của chính mình lên API. 
    - Tầng Service sử dụng `SecurityContextHolder.getContext().getAuthentication().getName()` để lấy tên đăng nhập từ Token JWT đang thực thi. Điều này ngăn chặn việc đăng ký hộ hoặc giả mạo ID người khác.
3.  **Logic nghiệp vụ**:
    - Tìm khóa học theo ID được gửi từ Client.
    - Thêm đối tượng `User` hiện tại vào danh sách `students` của `Course`.
    - Sử dụng `@Transactional` để đảm bảo dữ liệu được cập nhật đồng bộ và an toàn.
4.  **Endpoint (`StudentCourseController`)**:
    - API: `POST /api/v1/student/courses/{courseId}/register`.
    - Bảo vệ bằng quyền `STUDENT`.

---

## Tổng kết kỹ thuật:
- **Clean Architecture**: Chia tách rõ rệt Controller -> Service -> Repository.
- **Bảo mật**: Sử dụng JWT Stateless và phân quyền chặt chẽ trên từng Endpoint.
- **Hiệu năng**: Tích hợp AOP để giám sát thời gian thực hiện (FR-11) và Unit Test (FR-12) để đảm bảo chất lượng code.
