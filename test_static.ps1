$wc = New-Object System.Net.WebClient

# Test logo
try {
  $b = $wc.DownloadData('http://localhost:8080/images/logo.png')
  Write-Host "logo.png OK - $($b.Length) bytes"
} catch {
  Write-Host "logo.png FAIL: $($_.Exception.Message)"
}

# Test combo image
try {
  $b2 = $wc.DownloadData('http://localhost:8080/images/combos/cappuccino.jpg')
  Write-Host "cappuccino.jpg OK - $($b2.Length) bytes"
} catch {
  Write-Host "cappuccino.jpg FAIL: $($_.Exception.Message)"
}
