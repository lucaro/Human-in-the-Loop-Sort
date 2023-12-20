using CSV, DataFrames, CairoMakie, Statistics, ColorSchemes

files = sort(filter(x -> endswith(x, ".tsv"), readdir()), rev = true)

f = Figure()
ax = Axis(f[1, 1], xticks = 0:256:1024, ytickformat = v -> ["$(Int(x))" for x in v], xlabel = "Length of List", ylabel = "Number of Comparisons")

colors = ColorSchemes.Spectral_11
color_idx = 1

for file in files
    println(file)
    df = combine(groupby(CSV.read(file, DataFrame), :length), :comparisons => mean)
    lines!(ax, df[:, :length], df[:, :comparisons_mean], label = "$(parse(Int, file[11:13]))% Agreement", color = colors[color_idx]) 
    color_idx += 1
end

x = collect(8:1024)
nlogn = x .* log2.(x)

lines!(ax, x, nlogn, label = "1 nlogn", color = :gray, linestyle = :dot)
lines!(ax, x, 2 .* nlogn, label = "2 nlogn", color = :gray, linestyle = :dash)
lines!(ax, x, 3 .* nlogn, label = "3 nlogn", color = :gray, linestyle = :dashdot)
lines!(ax, x, 4 .* nlogn, label = "4 nlogn", color = :gray, linestyle = :dashdotdot)

axislegend(ax, position = :lt)

save("plot.png", f, px_per_unit = 2)