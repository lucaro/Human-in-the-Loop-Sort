using CSV, DataFrames, CairoMakie, Statistics

df_base = combine(groupby(CSV.read("base.tsv", DataFrame), :length), :comparisons => mean)
df_transitive = combine(groupby(CSV.read("transitive.tsv", DataFrame), :length), :comparisons => mean)
df_sublist = combine(groupby(CSV.read("sublist.tsv", DataFrame), :length), :comparisons => mean)
df_transitive_sublist = combine(groupby(CSV.read("transitive+sublist.tsv", DataFrame), :length), :comparisons => mean)

x = collect(8:1024)
nlogn = x .* log2.(x)

f = Figure()
ax = Axis(f[1, 1], xticks = 0:256:1024, yticks = 0:5000:30000, ytickformat = v -> ["$(Int(x))" for x in v], xlabel = "Length of List", ylabel = "Number of Comparisons")

lines!(ax, df_base[:, :length], df_base[:, :comparisons_mean], label = "Base") # ~2.8 nlogn
lines!(ax, df_transitive[:, :length], df_transitive[:, :comparisons_mean], label = "Transitive") # ~1.1 nlogn
lines!(ax, df_sublist[:, :length], df_sublist[:, :comparisons_mean], label = "Sublist") # ~2.2 nlogn
lines!(ax, df_transitive_sublist[:, :length], df_transitive_sublist[:, :comparisons_mean], label = "Transitive + Sublist")

lines!(ax, x, nlogn, label = "1 nlogn", color = :gray, linestyle = :dot)
lines!(ax, x, 2 .* nlogn, label = "2 nlogn", color = :gray, linestyle = :dash)
lines!(ax, x, 3 .* nlogn, label = "3 nlogn", color = :gray, linestyle = :dashdot)

axislegend(ax, position = :lt)

save("plot.png", f, px_per_unit = 2)