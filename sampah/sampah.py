size = 50
matrix = []

for i in range(size):
    row = []
    for j in range(size):
        if i == 0 or i == size-1 or j == 0 or j == size-1:
            row.append(1)
        else:
            row.append(0)
    matrix.append(row)

# Menampilkan matriks
for row in matrix:
    print(' '.join(map(str, row)))
