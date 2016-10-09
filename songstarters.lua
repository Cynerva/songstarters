require("formats")

local sounds = {}

function addSound(path)
  print("adding " .. path)
  local format = formats.u32be
  local rawStream = io.popen("ffmpeg -i " .. path .. " -f " .. format.text .. " -ac 2 -ar 44100 - 2>/dev/null", "r")
  local sound = {}
  local sample = format.read(rawStream)
  while sample ~= nil do
    table.insert(sound, sample)
    sample = format.read(rawStream)
  end
  table.insert(sounds, sound)
end

function addRandomSounds(count)
  local f = io.popen("ls -1 sounds")
  local names = {}
  for line in f:lines() do
    table.insert(names, line)
  end
  for i = 1,count do
    local name = names[math.random(#names)]
    local path = "sounds/" .. name
    addSound(path)
  end
end

local function getLoopLength(i)
  local length = 1
  while i % 2 == 0 do
    length = length * 2
    i = i / 2
  end
  return length
end

local function expandLoops(text)
  local result = ""
  for i = 1,#text do
    c = text:sub(i, i)
    if c == "!" then
      local length = getLoopLength(#result)
      result = result .. result:sub(#result - length + 1, #result)
    else
      result = result .. c
    end
  end
  return result
end

local function renderSongToStream(song, format, stream)
  local text = expandLoops(song.text)
  local tickDuration = 44100 * 2 * 60 / song.tempo / song.subdivide
  for i = 1,#text do
    local c = text:sub(i, i)
    local sound = sounds[tonumber(c) + 1]
    for j = 1,tickDuration do
      local sample = sound[j] or 0
      format.write(sample, stream)
    end
  end
end

function playSong(song)
  local format = formats.u32le
  local stream = io.popen("ffplay -nodisp -f " .. format.text .. " -ac 2 -ar 44100 -i pipe:0 2>/dev/null", "w")
  renderSongToStream(song, format, stream)
  stream:close()
end

function renderSong(song)
  local format = formats.u32le
  local stream = io.popen("ffmpeg -y -f " .. format.text .. " -ac 2 -ar 44100 -i pipe:0 render.flac 2>/dev/null", "w")
  renderSongToStream(song, format, stream)
  stream:close()
end

function printSong(song)
  print("song")
  print("| tempo " .. song.tempo)
  print("| subdivide " .. song.subdivide)
  print("| text " .. song.text)
end

function randomSongText(maxLength)
  local length = 0
  local result = ""
  while length < maxLength do
    if math.random() < 0.5 and length > 0 then
      local loopLength = getLoopLength(length)
      length = length + loopLength
      result = result .. "!"
    else
      length = length + 1
      result = result .. math.random(0, #sounds - 1)
    end
  end
  return result
end
