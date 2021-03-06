# HTTP Nukleus

The HTTP Nukleus is responsible for managing HTTP/1.1 inbound and outbound streams.

## Control Commands
The HTTP Nukleus is controlled by sending control commands to a memory-mapped file at `[config-dir]/http/control`.

The following control commands and responses are supported by the HTTP Nukleus.

### ERROR response (0x40000000)
Indicates that an error has occurred when attempting to process a command. 

```
ERROR
[long] correlation id
```

### CAPTURE command (0x00000001)
Creates, maps and reads streams from a memory-mapped file at `[config-dir]/http/streams/[source]`.

```
CAPTURE
[long] correlation id
[string] source nukleus name
```

### CAPTURED response (0x40000001)
Indicates that the CAPTURE command completed successfully. 

```
CAPTURED
[long] correlation id
```

### UNCAPTURE command (0x00000002)
No longer reads streams from the memory-mapped file at `[config-dir]/http/streams/[source]`.

```
UNCAPTURE
[long] correlation id
[string] source nukleus name
```

### UNCAPTURED response (0x40000002)
Indicates that the UNCAPTURE command completed successfully. 

```
UNCAPTURED
[long] correlation id
```

### ROUTE command (0x00000003)
Maps existing file and writes streams to a memory-mapped file at `[config-dir]/[destination]/streams/http`.

```
ROUTE
[long] correlation id
[string] destination nukleus name
```

### ROUTED response (0x40000003)
Indicates that the ROUTE command completed successfully. 

```
ROUTED
[long] correlation id
```

### UNROUTE command (0x00000004)
Unmaps and no longer writes streams to the memory-mapped file at `[config-dir]/[destination]/streams/http`.

```
UNROUTE
[long] correlation id
[string] destination nukleus name
```

### UNROUTED response (0x40000004)
Indicates that the UNROUTE command completed successfully. 

```
UNROUTED
[long] correlation id
```

### BIND command (0x00000011)
Binds incoming HTTP/1.1 protocol streams from the specified source nukleus with matching HTTP headers to the specified
destination nukleus and destination nukleus reference. 

```
BIND
[long] correlation id
[string] destination nukleus name
[long] destination nukleus reference
[string] source nukleus name
[headers] :host, :path, upgrade
```

### BOUND response (0x40000011)
Indicates that the BIND command completed successfully, returning a source reference. 

```
BOUND
[long] correlation id
[long] source reference
```

### UNBIND command (0x00000012)
No longer binds incoming HTTP/1.1 protocol streams to the previously bound source reference.

```
UNBIND
[long] correlation id
[long] source reference
```

### UNBOUND response (0x40000012)
Indicates that the UNBIND command completed successfully. 

```
UNBOUND
[long] correlation id
[string] destination nukleus name
[long] destination nukleus reference
[string] source nukleus name
[headers] :host, :path, upgrade
```

### PREPARE command (0x00000013)
Prepares incoming streams from the specified source nukleus to initiate HTTP/1.1 protocol streams with specified HTTP headers
to the specified destination nukleus and destination nukleus reference.

```
PREPARE
[long] correlation id
[string] source nukleus name
[string] destination nukleus name
[long] destination nukleus reference
[headers] host, :path, upgrade
```

### PREPARED response (0x40000013)
Indicates that the PREPARE command completed successfully, returning a source nukleus reference. 

```
PREPARED
[long] correlation id
[long] source nukleus reference
```

### UNPREPARE command (0x00000014)
No longer prepares incoming streams from the source nukleus for this source nukleus reference.

```
UNPREPARE
[long] correlation id
[long] source nukleus reference
```

### UNPREPARED response (0x40000014)
Indicates that the UNPREPARED command completed successfully. 

```
UNPREPARED
[long] correlation id
[string] source nukleus name
[string] destination nukleus name
[long] destination nukleus reference
[headers] :host, :path, upgrade
```

## Stream Events
The HTTP Nukleus describes unidirectional streams of data with the following events.

### RESET event (0x00000000)
Resets the stream as an error condition has occurred.

```
RESET
[long] stream id
```

### BEGIN event (0x00000001)
Indicates the beginning of a new stream.

If the stream identifier is odd, then the nukleus reference is required and it represents an HTTP request.
If the stream identifier is even and non-zero, then it represents an HTTP response, and the correlating HTTP 
request stream identifier is required instead.

```
BEGIN
[long] stream id
[long] nukleus reference | correlating stream id
[headers] :scheme, :method, :authority, :path, :status, host, ...
```

### DATA event (0x00000002)
Indicates data for an existing stream.

```
DATA
[long] stream id
[bytes] payload
```

### END event (0x00000003)
Indicates the end of an existing stream, including any HTTP trailers.

```
END
[long] stream id
[trailers] ...
```
