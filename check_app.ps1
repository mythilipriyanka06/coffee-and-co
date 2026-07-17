try {
  $r = Invoke-WebRequest -Uri 'http://localhost:8080/shop' -TimeoutSec 5
  Write-Host "Shop page status: $($r.StatusCode)"
} catch {
  Write-Host "Error reaching shop: $($_.Exception.Message)"
}
