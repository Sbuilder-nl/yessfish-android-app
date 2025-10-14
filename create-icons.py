#!/usr/bin/env python3
"""
Generate placeholder launcher icons for YessFish Android app.
These are temporary icons - replace with actual YessFish logo later.
"""

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    print("Error: PIL/Pillow not installed")
    print("Install with: pip3 install Pillow")
    exit(1)

import os

# Icon sizes for different densities
SIZES = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192
}

BASE_PATH = 'app/src/main/res'

# YessFish brand colors
BLUE = '#2196F3'  # YessFish blue
WHITE = '#FFFFFF'

def create_icon(size, output_path):
    """Create a simple placeholder icon with Y letter"""
    # Create blue background
    img = Image.new('RGB', (size, size), color=BLUE)
    draw = ImageDraw.Draw(img)

    # Draw white circle background
    margin = size // 6
    draw.ellipse([margin, margin, size-margin, size-margin], fill=WHITE)

    # Draw blue "Y" in center
    font_size = size // 2
    try:
        # Try different font locations
        font_paths = [
            "/usr/share/fonts/dejavu/DejaVuSans-Bold.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
            "/System/Library/Fonts/Helvetica.ttc",
        ]
        font = None
        for path in font_paths:
            if os.path.exists(path):
                font = ImageFont.truetype(path, font_size)
                break
        if font is None:
            font = ImageFont.load_default()
    except:
        font = ImageFont.load_default()

    text = "Y"
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    position = ((size - text_width) // 2, (size - text_height) // 2 - size//20)
    draw.text(position, text, fill=BLUE, font=font)

    # Save icon
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    img.save(output_path, 'PNG')
    print(f'✅ Created: {output_path} ({size}x{size})')

def main():
    print("🎨 Creating YessFish launcher icons...")
    print("⚠️  Note: These are PLACEHOLDER icons")
    print("    Replace with actual YessFish logo later\n")

    for density, size in SIZES.items():
        folder = f'{BASE_PATH}/mipmap-{density}'

        # Create launcher icon
        create_icon(size, f'{folder}/ic_launcher.png')

        # Create round launcher icon (same for now)
        create_icon(size, f'{folder}/ic_launcher_round.png')

    print('\n✅ All launcher icons created successfully!')
    print('\n📝 Next steps:')
    print('   1. Test the build: gradlew.bat assembleDebug')
    print('   2. Later: Replace with actual YessFish logo')
    print('   3. See: ICON-CREATION-GUIDE.md for instructions')

if __name__ == '__main__':
    main()
