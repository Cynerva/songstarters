require("formats")

local sounds = {}

local function addSound(path)
  local format = formats.u32be
  local rawStream = io.popen("ffmpeg -i " .. path .. " -f " .. format.text .. " -ac 2 -ar 44100 -", "r")
  local sound = {}
  local sample = format.read(rawStream)
  while sample ~= nil do
    table.insert(sound, sample)
    sample = format.read(rawStream)
  end
  table.insert(sounds, sound)
end

local function expandLoops(text)
  local result = ""
  for i = 1,#text do
    c = text:sub(i, i)
    if c == "!" then
      local length = 1
      local index = #result
      while index % 2 == 0 do
        length = length * 2
        index = index / 2
      end
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

local function playSong(song)
  local format = formats.u32le
  local stream = io.popen("ffplay -nodisp -f " .. format.text .. " -ac 2 -ar 44100 -i pipe:0", "w")
  renderSongToStream(song, format, stream)
  stream:close()
end

local function renderSong(song)
  local format = formats.u32le
  local stream = io.popen("ffmpeg -f " .. format.text .. " -ac 2 -ar 44100 -i pipe:0 render.flac", "w")
  renderSongToStream(song, format, stream)
  stream:close()
end

addSound("kick.flac")
addSound("hat.flac")
addSound("snare.flac")

playSong({
  text="01!2!!!",
  tempo=120,
  subdivide=4
})
