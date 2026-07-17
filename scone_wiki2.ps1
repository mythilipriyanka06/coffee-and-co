$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$tgt = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\target\classes\static\images\combos'

# Use .NET HttpClient with proper headers that Wikimedia accepts
Add-Type -AssemblyName System.Net.Http
$client = New-Object System.Net.Http.HttpClient
$client.DefaultRequestHeaders.Add('User-Agent', 'CafeWebsite/1.0 (educational project; mythili.dev@gmail.com)')
$client.DefaultRequestHeaders.Add('Accept', 'image/jpeg,image/*')

$urls = @(
  'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Baked_scones_serving.jpg/800px-Baked_scones_serving.jpg',
  'https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Scone_2009.jpg/800px-Scone_2009.jpg',
  'https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Scones_with_jam_and_cream.jpg/640px-Scones_with_jam_and_cream.jpg'
)
$labels = @('scone_wiki1','scone_plain','scone_jam')

Start-Sleep -Seconds 3

for ($i = 0; $i -lt $urls.Count; $i++) {
  try {
    $task  = $client.GetByteArrayAsync($urls[$i])
    $bytes = $task.GetAwaiter().GetResult()
    if ($bytes.Length -gt 20000) {
      $path = "$dir\$($labels[$i]).jpg"
      [System.IO.File]::WriteAllBytes($path, $bytes)
      Write-Host "OK: $($labels[$i]) $($bytes.Length)"
    }
  } catch {
    Write-Host "FAIL $($labels[$i]): $($_.Exception.Message)"
  }
  Start-Sleep -Seconds 2
}
$client.Dispose()
Write-Host 'Done'
