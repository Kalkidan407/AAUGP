# Department Field Changes - Registration Fix

## Problem Fixed
The registration endpoint was throwing "A unique field already exists" error for department during user registration. This was caused by the `resolveDepartment` method trying to create new department entries when multiple users registered with the same department name simultaneously, causing unique constraint violations.

## Solution Implemented
Changed the department field from a **String (department name)** to a **Long (department ID)** throughout the application. Users now select from existing departments via a dropdown menu instead of typing the department name.

## Changes Made

### 1. **RegisterRequest.java**
- Changed `department` field from `String` to `Long departmentId`
- Updated to accept department ID during registration

### 2. **UserRequest.java**
- Changed `departments` field from `String` to `Long departmentId`
- Updated to accept department ID for user creation/updates

### 3. **UserResponse.java**
- Added `departmentId` field (Long) alongside existing `departments` field (String)
- Now returns both department name (for display) and department ID (for updates)

### 4. **AuthService.java**
- Updated `toUserRequest()` method to pass `departmentId` instead of `department`

### 5. **UserServices.java**
- Replaced `resolveDepartment(String departmentName)` with `resolveDepartmentById(Long departmentId)`
- New method simply looks up department by ID instead of creating new ones
- Updated `fromDTO()` and `updateUser()` methods to use department ID
- Updated `toDTO()` method to return both department name and ID

## How to Use

### For Frontend/API Consumers

#### 1. Get Available Departments (for dropdown)
```http
GET /api/department
```

Response:
```json
{
  "content": [
    {
      "id": 1,
      "departmentName": "Computer Science"
    },
    {
      "id": 2,
      "departmentName": "Software Engineering"
    }
  ]
}
```

#### 2. Register a New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Abebe Kebede",
  "email": "abebe@example.com",
  "departmentId": 1,
  "studentId": "UGR/1234/23",
  "password": "StrongPassword123"
}
```

#### 3. Update User
```http
PUT /api/user/update/{id}
Content-Type: application/json

{
  "name": "Abebe Kebede",
  "email": "abebe@example.com",
  "departmentId": 2,
  "studentId": "UGR/1234/23",
  "password": "NewPassword123"
}
```

### For Admins

#### Create New Department (if needed)
```http
POST /api/department/create
Content-Type: application/json

{
  "departmentName": "Electrical Engineering"
}
```

## Benefits

1. ✅ **No More Duplicate Department Errors** - Users select from existing departments
2. ✅ **Better Data Integrity** - Prevents typos and inconsistent department names
3. ✅ **Improved UX** - Dropdown menu is easier than typing
4. ✅ **Centralized Department Management** - Admins can manage departments separately
5. ✅ **Backward Compatible Response** - UserResponse still includes department name for display

## Migration Notes

If you have existing data:
- Existing users with departments will continue to work
- The department name is still returned in API responses for display purposes
- New registrations and updates now require a valid department ID

## Testing

The application has been compiled successfully with all changes. To test:

1. Start the application: `mvn spring-boot:run`
2. Access Swagger UI at: `http://localhost:8080/swagger-ui.html`
3. Test the registration endpoint with a department ID
4. Verify the department dropdown works correctly in your frontend
