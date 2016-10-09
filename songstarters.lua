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

addSound("test.ogg")
local format = formats.u32le
local f = io.popen("ffplay -nodisp -f " .. format.text .. " -ac 2 -ar 44100 -i pipe:0", "w")
for _,sample in ipairs(sounds[1]) do
  format.write(sample, f)
end
