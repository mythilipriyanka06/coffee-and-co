$wc   = New-Object System.Net.WebClient
$base = 'http://localhost:8080/images/combos/'
$imgs = @('garlic_bread.jpg','latte.jpg','mocha.jpg','scone.jpg','lemon_cake.jpg')
foreach ($img in $imgs) {
  try {
    $b = $wc.DownloadData($base + $img)
    Write-Host "OK: $img ($($b.Length) bytes)"
  } catch {
    Write-Host "FAIL: $img - $($_.Exception.Message)"
  }
}
