formats = {}

formats.u8 = {
  text="u8",
  read=function(stream)
    local c = stream:read(1)
    if c == nil then return nil end
    return c:byte() / 128 - 1
  end,
  write=function(sample, stream)
    local byte = math.floor((sample + 1) * 128)
    local c = string.char(byte)
    stream:write(c)
  end
}

formats.u32be = {
  text="u32be",
  read=function(stream)
    local chars = stream:read(4)
    if chars == nil then return nil end
    local value = 0
    for i = 1,4 do
      value = value * 256 + chars:sub(i, i):byte()
    end
    return value / 2147483648 - 1
  end
}

formats.u32le = {
  text="u32le",
  write=function(sample, stream)
    local value = math.floor((sample + 1) * 2147483648)
    for i = 1,4 do
      stream:write(string.char(value % 256))
      value = value / 256
    end
  end
}
