param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Username = "admin",
    [string]$Password = "123456"
)

$ErrorActionPreference = "Stop"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Assert-ApiSuccess {
    param(
        [object]$Response,
        [string]$Name
    )
    if ($null -eq $Response) {
        throw "$Name failed: empty response"
    }
    if ($Response.code -ne 200) {
        $json = $Response | ConvertTo-Json -Depth 10
        throw "$Name failed: $json"
    }
    Write-Host "[PASS] $Name" -ForegroundColor Green
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        [object]$Body = $null,
        [hashtable]$Headers = @{}
    )

    $uri = "$BaseUrl$Path"
    $params = @{
        Method      = $Method
        Uri         = $uri
        Headers     = $Headers
        ContentType = "application/json"
    }

    if ($null -ne $Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 20)
    }

    try {
        return Invoke-RestMethod @params
    } catch {
        if ($_.ErrorDetails.Message) {
            Write-Host $_.ErrorDetails.Message -ForegroundColor Red
        } elseif ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $body = $reader.ReadToEnd()
            if ($body) {
                Write-Host $body -ForegroundColor Red
            }
        }
        throw
    }
}

Write-Host "Backend A API smoke test" -ForegroundColor Yellow
Write-Host "BaseUrl: $BaseUrl"

$suffix = Get-Date -Format "yyyyMMddHHmmss"

Write-Step "1. Login"
$loginResp = Invoke-Api -Method "POST" -Path "/api/auth/login" -Body @{
    username = $Username
    password = $Password
}
Assert-ApiSuccess $loginResp "POST /api/auth/login"

$token = $loginResp.data.accessToken
if ([string]::IsNullOrWhiteSpace($token)) {
    throw "Login succeeded but accessToken is empty"
}

$authHeaders = @{
    Authorization = "Bearer $token"
}

Write-Step "2. Query menus"
$menusResp = Invoke-Api -Method "GET" -Path "/api/menus" -Headers $authHeaders
Assert-ApiSuccess $menusResp "GET /api/menus"

$allMenuIds = @()
function Collect-MenuIds {
    param([object[]]$Menus)
    foreach ($menu in $Menus) {
        $script:allMenuIds += [int64]$menu.id
        if ($menu.children) {
            Collect-MenuIds -Menus $menu.children
        }
    }
}
Collect-MenuIds -Menus $menusResp.data
if ($allMenuIds.Count -eq 0) {
    throw "GET /api/menus succeeded but no menu IDs were returned"
}

Write-Step "3. Create test menu"
$testMenuPermission = "test:backend-a:$suffix"
$createMenuResp = Invoke-Api -Method "POST" -Path "/api/menus" -Headers $authHeaders -Body @{
    parentId        = 0
    menuName        = "测试菜单$suffix"
    menuType        = "BUTTON"
    path            = $null
    component       = $null
    permissionCode  = $testMenuPermission
    sortNo          = 999
    status          = 1
}
Assert-ApiSuccess $createMenuResp "POST /api/menus"
$testMenuId = [int64]$createMenuResp.data.id

Write-Step "4. Update test menu"
$updateMenuResp = Invoke-Api -Method "PUT" -Path "/api/menus/$testMenuId" -Headers $authHeaders -Body @{
    parentId        = 0
    menuName        = "测试菜单已更新$suffix"
    menuType        = "BUTTON"
    path            = $null
    component       = $null
    permissionCode  = $testMenuPermission
    sortNo          = 1000
    status          = 1
}
Assert-ApiSuccess $updateMenuResp "PUT /api/menus/{id}"

Write-Step "5. Query roles"
$rolesResp = Invoke-Api -Method "GET" -Path "/api/roles" -Headers $authHeaders
Assert-ApiSuccess $rolesResp "GET /api/roles"

Write-Step "6. Create test role"
$roleCode = "TEST_$suffix"
$createRoleResp = Invoke-Api -Method "POST" -Path "/api/roles" -Headers $authHeaders -Body @{
    roleCode = $roleCode
    roleName = "测试角色$suffix"
    status   = 1
    remark   = "API smoke test role"
}
Assert-ApiSuccess $createRoleResp "POST /api/roles"
$roleId = [int64]$createRoleResp.data.id

Write-Step "7. Update test role"
$updateRoleResp = Invoke-Api -Method "PUT" -Path "/api/roles/$roleId" -Headers $authHeaders -Body @{
    roleCode = $roleCode
    roleName = "测试角色已更新$suffix"
    status   = 1
    remark   = "API smoke test role updated"
}
Assert-ApiSuccess $updateRoleResp "PUT /api/roles/{id}"

Write-Step "8. Assign menus to test role"
$menuIdsForRole = @($testMenuId)
if ($allMenuIds.Count -gt 0) {
    $menuIdsForRole += [int64]$allMenuIds[0]
}
$assignMenusResp = Invoke-Api -Method "PUT" -Path "/api/roles/$roleId/menus" -Headers $authHeaders -Body @{
    menuIds = $menuIdsForRole
}
Assert-ApiSuccess $assignMenusResp "PUT /api/roles/{id}/menus"

Write-Step "9. Query users"
$usersResp = Invoke-Api -Method "GET" -Path "/api/users?pageNum=1&pageSize=10" -Headers $authHeaders
Assert-ApiSuccess $usersResp "GET /api/users"

Write-Step "10. Create test user"
$testUsername = "test_user_$suffix"
$createUserResp = Invoke-Api -Method "POST" -Path "/api/users" -Headers $authHeaders -Body @{
    username = $testUsername
    password = "123456"
    realName = "测试用户$suffix"
    mobile   = "13800138000"
    email    = "$testUsername@example.com"
    status   = 1
    roleIds  = @($roleId)
}
Assert-ApiSuccess $createUserResp "POST /api/users"
$userId = [int64]$createUserResp.data.id

Write-Step "11. Update test user"
$updateUserResp = Invoke-Api -Method "PUT" -Path "/api/users/$userId" -Headers $authHeaders -Body @{
    username = $testUsername
    password = ""
    realName = "测试用户已更新$suffix"
    mobile   = "13800138001"
    email    = "$testUsername.updated@example.com"
    status   = 1
    roleIds  = @($roleId)
}
Assert-ApiSuccess $updateUserResp "PUT /api/users/{id}"

Write-Step "12. Disable and enable test user"
$disableUserResp = Invoke-Api -Method "PUT" -Path "/api/users/$userId/status?status=0" -Headers $authHeaders
Assert-ApiSuccess $disableUserResp "PUT /api/users/{id}/status disable"

$enableUserResp = Invoke-Api -Method "PUT" -Path "/api/users/$userId/status?status=1" -Headers $authHeaders
Assert-ApiSuccess $enableUserResp "PUT /api/users/{id}/status enable"

Write-Step "13. Query operation logs"
$logsResp = Invoke-Api -Method "GET" -Path "/api/logs/operations?pageNum=1&pageSize=20" -Headers $authHeaders
Assert-ApiSuccess $logsResp "GET /api/logs/operations"

Write-Step "14. Delete test menu"
$deleteMenuResp = Invoke-Api -Method "DELETE" -Path "/api/menus/$testMenuId" -Headers $authHeaders
Assert-ApiSuccess $deleteMenuResp "DELETE /api/menus/{id}"

Write-Host ""
Write-Host "All Backend A API smoke tests passed." -ForegroundColor Green
Write-Host "Created test role: $roleCode (id=$roleId)"
Write-Host "Created test user: $testUsername (id=$userId)"
