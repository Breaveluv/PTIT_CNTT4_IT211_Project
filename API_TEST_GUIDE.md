# 📘 CẨM NANG KIỂM THỬ CHI TIẾT (PHIÊN BẢN CẬP NHẬT)

Tài liệu này hướng dẫn kiểm thử hệ thống theo kịch bản thực tế.

---

## 🟢 PHẦN 1: TÀI KHOẢN (FR-01, FR-04)

### Case 1.1: Đăng ký Sinh viên mới
- **API**: `POST /api/auth/register`
- **Body**:
```json
{
  "username": "sinhvien01",
  "password": "password123",
  "fullName": "Nguyễn Văn Sinh Viên"
}
```

### Case 1.2: Đăng nhập Admin
- **API**: `POST /api/auth/login`
- **Body**: `{"username": "admin", "password": "admin123"}`
- **Hành động**: Lấy Token Admin cho Phần 2.

---

## 🔵 PHẦN 2: QUẢN TRỊ VIÊN - ADMIN (FR-05)

### Case 2.1: Tạo Khóa học mới (QUAN TRỌNG)
- **API**: `POST /api/v1/admin/courses`
- **Body (Đã sửa lỗi null)**:
```json
{
  "courseCode": "JAVA2024",
  "courseName": "Lập trình Java Web Nâng Cao",
  "description": "Hướng dẫn Spring Boot và RESTful API",
  "credit": 3
}
```

---

## 🟡 PHẦN 3: NGHIỆP VỤ SINH VIÊN (FR-06, FR-07)

### Case 3.1: Đăng ký tham gia khóa học
- **API**: `POST /api/v1/student/courses/1/register`

### Case 3.2: Nộp link GitHub đồ án
- **API**: `POST /api/v1/student/submissions`
- **Body**:
```json
{
  "courseId": 1,
  "githubRepoUrl": "https://github.com/sinhvien01/final-project"
}
```

---

## 👨‍🏫 PHẦN 4: GIẢNG VIÊN - LECTURER (FR-08)

### Case 4.1: Chấm điểm & Nhật ký AOP
- **API**: `PUT /api/v1/lecturer/grades/1`
- **Body**: `{"score": 95, "feedback": "Rất tốt!"}`
- **Kiểm tra Log (AOP)**: Xem Console IDE.

---
**Lưu ý**: Nếu bạn vẫn dùng `ddl-auto=create`, mỗi lần Restart app dữ liệu cũ sẽ mất. Hãy đổi lại thành `update` sau khi app đã chạy ổn định.
