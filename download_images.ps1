$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'

$images = @(
  @("garlic_bread",        "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=600&q=80"),
  @("brownie",             "https://images.unsplash.com/photo-1606312619070-d48b4c652a52?w=600&q=80"),
  @("latte",               "https://images.unsplash.com/photo-1561882468-9110d70d0782?w=600&q=80"),
  @("chocolate_croissant", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=600&q=80"),
  @("mocha",               "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600&q=80"),
  @("cheese_tart",         "https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=600&q=80"),
  @("americano",           "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&q=80"),
  @("blueberry_muffin",    "https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=600&q=80"),
  @("flat_white",          "https://images.unsplash.com/photo-1577968897966-3d4325b36b61?w=600&q=80"),
  @("scone",               "https://images.unsplash.com/photo-1621997895729-64e0dd0c5d76?w=600&q=80"),
  @("cold_brew",           "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=600&q=80"),
  @("chocolate_cookie",    "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=600&q=80"),
  @("tea",                 "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=600&q=80"),
  @("lemon_cake",          "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600&q=80")
)

foreach ($img in $images) {
  $name = $img[0]
  $url  = $img[1]
  $path = "$dir\$name.jpg"
  try {
    Invoke-WebRequest -Uri $url -OutFile $path -TimeoutSec 30 -UserAgent 'Mozilla/5.0'
    Write-Host "OK: $name"
  } catch {
    Write-Host "FAIL: $name - $_"
  }
}

Write-Host "All done."
