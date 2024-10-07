# ZPIapi

API do komunikacji z bazą danych wykorzystywane w aplikacji mobilnej.

## Uruchamianie

Aby uruchomić lokalnie:
* zmień wartość DATABASE_URL w app.py na pusty string (DATABASE_URL = '')
* zmień metodę otrzymania connection w testowanym przez ciebie endpointcie na get_connection_local()
* uruchom aplikacjię jak zwykle

Aby uruchomić w chmurze:
* zmień wartość DATABASE_URL w app.py na os.environ['DATABASE_URL'
* zmień metodę otrzymania connection w każdym endpointcie na get_connection()

Przed każdym pushem na branch main upewnij się, że API jest przystosowane do działania w chmurze!
